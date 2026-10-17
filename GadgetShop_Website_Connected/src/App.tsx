import { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import type { FormEvent, ReactNode } from 'react';

import { BatteryCharging, ClipboardList, MapPin, MessageSquare, Phone, Store, Activity, ArrowRight, CalendarDays, Check, ChevronDown, CircleUserRound, Cpu, Database, Headphones, Laptop, LogIn, LogOut, Mail, Menu, Package, Plus, Search, ShieldCheck, ShoppingBag, Smartphone, Sparkles, Tag, Trash2, Wrench, X } from 'lucide-react';



const API = (import.meta.env.VITE_API_BASE_URL || 'http://localhost/gadget_shop').replace(/\/$/, '');

const adminEndpoints = new Set(['add_product.php', 'update_product.php', 'delete_product.php', 'add_category.php', 'update_category.php', 'delete_category.php', 'update_appointment_status.php', 'get_all_appointments.php', 'get_customers.php', 'get_all_inquiries.php', 'get_inquiry_notifications.php', 'mark_inquiries_read.php', 'admin_logout.php']);
type Raw = Record<string, any>;

type Product = { id: number; name: string; brand: string; description: string; price: number; stock: number; categoryId: number; category: string; image: string };

type Category = { id: number; name: string; description: string; count?: number };

type Customer = { id: number; firstName: string; lastName: string; email: string; contact: string; address?: string; street?: string; barangay?: string; city?: string; postal_code?: string };

type Employee = { id: number; fullName: string; username: string; role: string };

type Appointment = { id: number; customerId: number; firstName: string; lastName: string; email: string; model: string; type: string; service: string; repairNotes: string; repairTotal: number | null; readyAt: string; date: string; time: string; status: string };

type Inquiry = { id: number; customerId: number; name: string; email: string; message: string; date: string; isNew: boolean };

type Notice = { type: 'ok' | 'error'; text: string };



async function request<T=Raw>(path: string, method = 'GET', body?: unknown): Promise<T> {

  let response: Response;

  try {

    const endpoint = path.split('?')[0];
    const key = adminEndpoints.has(endpoint) ? 'gadgetAdminToken' : 'gadgetCustomerToken';
    const token = sessionStorage.getItem(key);
    const headers: Record<string, string> = body ? { 'Content-Type': 'application/json' } : {};
    if (token && !['login.php', 'admin_login.php', 'register.php', 'request_password_reset.php', 'verify_password_reset.php', 'reset_password.php'].includes(endpoint)) headers.Authorization = `Bearer ${token}`;
    response = await fetch(`${API}/${endpoint === 'admin_logout.php' ? 'logout.php' : path}`, { method, headers, body: body ? JSON.stringify(body) : undefined });

  } catch { throw new Error(`Cannot reach the PHP API at ${API}. Check that XAMPP Apache is running and the API URL is correct.`); }

  const data = await response.json().catch(() => { throw new Error('The PHP API returned an invalid response. Check the Apache error log.'); });
  if (response.status === 401) {
    const key = adminEndpoints.has(path.split('?')[0]) ? 'gadgetAdminToken' : 'gadgetCustomerToken';
    sessionStorage.removeItem(key);
    localStorage.removeItem(key === 'gadgetAdminToken' ? 'gadgetEmployee' : 'gadgetCustomer');
    window.dispatchEvent(new Event('gadget-session-expired'));
  }

  if (!response.ok || data.success === false) throw new Error(data.message || `API request failed (${response.status}).`);

  return data as T;

}

const num = (v: unknown) => Number(v || 0);

const mapProduct = (p: Raw): Product => ({ id: num(p.product_id), name: p.product_name || '', brand: p.brand || '', description: p.description || '', price: num(p.price), stock: num(p.stock_quantity), categoryId: num(p.category_id), category: p.category_name || '', image: p.image_url || '' });

const mapCategory = (c: Raw): Category => ({ id: num(c.category_id), name: c.category_name || '', description: c.description || '', count: num(c.product_count) });

const mapAppointment = (a: Raw): Appointment => ({ id: num(a.appointment_id), customerId: num(a.customer_id), firstName: a.first_name || '', lastName: a.last_name || '', email: a.email || '', model: a.device_model || '', type: a.device_type || '', service: a.service_description || '', repairNotes: a.repair_notes || '', repairTotal: a.repair_total == null ? null : num(a.repair_total), readyAt: a.ready_to_claim_at || '', date: a.appointment_date || '', time: a.appointment_time || '', status: a.status || 'Pending' });

const money = (v: number) => new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', maximumFractionDigits: 2 }).format(v);

const niceDate = (v: string) => v ? new Date(`${v}T00:00:00`).toLocaleDateString('en-PH', { year: 'numeric', month: 'short', day: 'numeric' }) : '—';

const nextStatuses: Record<string, string[]> = {

  Pending: ['Approved', 'Cancelled'],

  Approved: ['Ready to Claim'],

  'Ready to Claim': ['Completed'],

  Completed: [],

  Cancelled: []

};



export default function App() {

  const [page, setPage] = useState('home');
  const [claimForm, setClaimForm] = useState<Appointment | null>(null);
  const [claimSaving, setClaimSaving] = useState(false);

  const [products, setProducts] = useState<Product[]>([]);

  const [categories, setCategories] = useState<Category[]>([]);

  const [customer, setCustomer] = useState<Customer | null>(() => { try { return sessionStorage.getItem('gadgetCustomerToken') ? JSON.parse(localStorage.getItem('gadgetCustomer') || 'null') : null; } catch { return null; } });

  const [employee, setEmployee] = useState<Employee | null>(() => { try { return sessionStorage.getItem('gadgetAdminToken') ? JSON.parse(localStorage.getItem('gadgetEmployee') || 'null') : null; } catch { return null; } });

  const [appointments, setAppointments] = useState<Appointment[]>([]);

  const [customers, setCustomers] = useState<Raw[]>([]);

  const [inquiries, setInquiries] = useState<Inquiry[]>([]);
  const [unreadInquiries, setUnreadInquiries] = useState<number | null>(null);
  const [inquiryCheckFailed, setInquiryCheckFailed] = useState(false);
  const inquiryRequestVersion = useRef(0);

  const [notice, setNotice] = useState<Notice | null>(null);

  const [busy, setBusy] = useState(false);

  const [query, setQuery] = useState('');

  const [categoryFilter, setCategoryFilter] = useState('all');

  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

  const [authMode, setAuthMode] = useState<'login'|'register'|'admin'>('login');

  const [showMobileNav, setShowMobileNav] = useState(false);

  const [productForm, setProductForm] = useState<Partial<Product> | null>(null);

  const [categoryForm, setCategoryForm] = useState<Partial<Category> | null>(null);



  useEffect(() => {
    const expired = () => {
      if (!sessionStorage.getItem('gadgetCustomerToken')) setCustomer(null);
      if (!sessionStorage.getItem('gadgetAdminToken')) setEmployee(null);
      setNotice({ type: 'error', text: 'Your session expired. Please sign in again.' });
      setPage('auth');
    };
    window.addEventListener('gadget-session-expired', expired);
    return () => window.removeEventListener('gadget-session-expired', expired);
  }, []);
  const [resetStep, setResetStep] = useState<'email' | 'code' | 'password'>('email');
  const [resetEmail, setResetEmail] = useState('');
  const [resetRequestId, setResetRequestId] = useState('');
  const [resetToken, setResetToken] = useState('');
  const tell = (type: Notice['type'], text: string) => { setNotice({ type, text }); window.scrollTo({ top: 0, behavior: 'smooth' }); };

  const loadProducts = useCallback(async () => { const d = await request<{products: Raw[]}>('products.php'); setProducts((d.products || []).map(mapProduct)); }, []);

  const loadCategories = useCallback(async () => { const d = await request<{categories: Raw[]}>('get_categories.php'); setCategories((d.categories || []).map(mapCategory)); }, []);

  const loadCustomerAppointments = useCallback(async (id: number) => { const d = await request<{appointments: Raw[]}>(`get_appointments.php?customer_id=${id}`); setAppointments((d.appointments || []).map(mapAppointment)); }, []);

  const loadAdminData = useCallback(async () => {

    const results = await Promise.allSettled([

      request<{appointments: Raw[]}>('get_all_appointments.php'), request<{customers: Raw[]}>('get_customers.php'), request<{inquiries: Raw[]}>('get_all_inquiries.php'), loadProducts(), loadCategories()

    ]);

    const failed = results.find(result => result.status === 'rejected');
    if (failed?.status === 'rejected') tell('error', failed.reason?.message || 'Could not load admin data.');
    if (results[0].status === 'fulfilled') setAppointments((results[0].value.appointments || []).map(mapAppointment));

    if (results[1].status === 'fulfilled') setCustomers(results[1].value.customers || []);

    if (results[2].status === 'fulfilled') setInquiries((results[2].value.inquiries || []).map((i: Raw) => ({ id: num(i.inquiry_id), customerId: num(i.customer_id), name: `${i.first_name || ''} ${i.last_name || ''}`.trim(), email: i.email || '', message: i.message || '', date: i.date_submitted || '', isNew: i.is_new === true })));

  }, [loadCategories, loadProducts]);

  useEffect(() => { Promise.allSettled([loadProducts(), loadCategories()]).then((r) => { if (r.every(x => x.status === 'rejected')) tell('error', 'Could not load the live catalog. Check XAMPP and the API URL.'); }); }, [loadProducts, loadCategories]);

  useEffect(() => { if (customer && page === 'appointments') loadCustomerAppointments(customer.id).catch(e => tell('error', e.message)); }, [customer, page, loadCustomerAppointments]);

  useEffect(() => { if (employee && page.startsWith('admin')) loadAdminData(); }, [employee, page, loadAdminData]);



  // Poll only on admin pages; invalidate in-flight results on navigation/sign-out.
  useEffect(() => {
    if (!employee || !page.startsWith('admin')) { setUnreadInquiries(null); return; }
    let active = true;
    let checking = false;
    async function check() {
      if (checking) return; checking = true;
      const version = ++inquiryRequestVersion.current;
      try {
        const d = await request<{unread_count: number}>('get_inquiry_notifications.php');
        if (!active || version !== inquiryRequestVersion.current) return;
        setUnreadInquiries(num(d.unread_count)); setInquiryCheckFailed(false);
        if (page === 'admin-inquiries') {
          const list = await request<{inquiries: Raw[]}>('get_all_inquiries.php');
          if (active) setInquiries((list.inquiries || []).map((i: Raw) => ({ id: num(i.inquiry_id), customerId: num(i.customer_id), name: `${i.first_name || ''} ${i.last_name || ''}`.trim(), email: i.email || '', message: i.message || '', date: i.date_submitted || '', isNew: i.is_new === true })));
        }
      } catch { if (active) setInquiryCheckFailed(true); }
      finally { checking = false; }
    }
    void check();
    const timer = window.setInterval(() => { if (!document.hidden) void check(); }, 15000);
    const focused = () => { void check(); };
    window.addEventListener('focus', focused);
    return () => { active = false; ++inquiryRequestVersion.current; window.clearInterval(timer); window.removeEventListener('focus', focused); };
  }, [employee?.id, page]);

  useEffect(() => {
    if (!employee || page !== 'admin-inquiries') return;
    const ids = inquiries.filter(i => i.isNew).map(i => i.id);
    if (!ids.length) return;
    let active = true;
    // Split large lists into bounded acknowledgments; do not mark unseen arrivals.
    async function markSeen() {
      ++inquiryRequestVersion.current;
      try {
        let lastCount = 0;
        for (let i = 0; i < ids.length; i += 10000) {
          if (!active) return;
          const d = await request<{unread_count: number}>('mark_inquiries_read.php', 'POST', { inquiry_ids: ids.slice(i, i + 10000) });
          lastCount = num(d.unread_count);
        }
        if (active) {
          setInquiries(current => current.map(i => ids.includes(i.id) ? {...i, isNew: false} : i));
          setUnreadInquiries(lastCount); setInquiryCheckFailed(false);
        }
      } catch { if (active) setInquiryCheckFailed(true); }
    }
    void markSeen();
    return () => { active = false; };
  }, [employee?.id, page, inquiries]);

  const visibleProducts = useMemo(() => products.filter(p => (categoryFilter === 'all' || String(p.categoryId) === categoryFilter) && `${p.name} ${p.brand} ${p.category}`.toLowerCase().includes(query.toLowerCase())), [products, categoryFilter, query]);

  const go = (next: string) => { setPage(next); setNotice(null); setShowMobileNav(false); setSelectedProduct(null); window.scrollTo({ top: 0, behavior: 'smooth' }); };

  const refreshCatalog = async () => { await Promise.all([loadProducts(), loadCategories()]); };



  async function submitAuth(e: FormEvent<HTMLFormElement>) {

    e.preventDefault(); setBusy(true); const fd = new FormData(e.currentTarget);

    try {

      if (authMode === 'admin') {

        const d = await request<Raw>('admin_login.php', 'POST', { username: fd.get('username'), password: fd.get('password') });

        if (!d.token) throw new Error('Update the PHP login endpoint to enable authorization.');
        sessionStorage.setItem('gadgetAdminToken', d.token);
        sessionStorage.removeItem('gadgetCustomerToken'); localStorage.removeItem('gadgetCustomer'); setCustomer(null);
        const raw = d.employee || {}; const emp = { id: num(raw.employee_id), fullName: raw.full_name || 'Admin', username: raw.username || '', role: raw.role || 'Admin' };

        setEmployee(emp); localStorage.setItem('gadgetEmployee', JSON.stringify(emp)); go('admin'); tell('ok', `Welcome back, ${emp.fullName}.`);

      } else if (authMode === 'register') {

        const d = await request<Raw>('register.php', 'POST', { first_name: fd.get('firstName'), last_name: fd.get('lastName'), email: fd.get('email'), password: fd.get('password') });

        if (!d.token) throw new Error('Update the PHP login/register endpoints to enable authorization.');
        sessionStorage.setItem('gadgetCustomerToken', d.token);
        sessionStorage.removeItem('gadgetAdminToken'); localStorage.removeItem('gadgetEmployee'); setEmployee(null);
        const c = { id: num(d.customer_id), firstName: String(fd.get('firstName') || ''), lastName: String(fd.get('lastName') || ''), email: String(fd.get('email') || ''), contact: '' };

        setCustomer(c); localStorage.setItem('gadgetCustomer', JSON.stringify(c)); go('home'); tell('ok', 'Account created successfully.');

      } else {

        const d = await request<Raw>('login.php', 'POST', { email: fd.get('email'), password: fd.get('password') });

        if (!d.token) throw new Error('Update the PHP login/register endpoints to enable authorization.');
        sessionStorage.setItem('gadgetCustomerToken', d.token);
        sessionStorage.removeItem('gadgetAdminToken'); localStorage.removeItem('gadgetEmployee'); setEmployee(null);
        const c = { id: num(d.customer_id), firstName: d.first_name || '', lastName: d.last_name || '', email: d.email || '', contact: d.contact_number || '', address: d.address || '' };

        setCustomer(c); localStorage.setItem('gadgetCustomer', JSON.stringify(c)); go('home'); tell('ok', `Welcome back, ${c.firstName}.`);

      }

    } catch (err) { tell('error', err instanceof Error ? err.message : 'Something went wrong.'); }

    finally { setBusy(false); }

  }

  async function logout() {
    try { await request(employee ? 'admin_logout.php' : 'logout.php', 'POST', {}); }
    catch (e) { tell('error', e instanceof Error ? e.message : 'Could not revoke session.'); return; }
    setCustomer(null); setEmployee(null);
    sessionStorage.removeItem('gadgetCustomerToken'); sessionStorage.removeItem('gadgetAdminToken');
    localStorage.removeItem('gadgetCustomer'); localStorage.removeItem('gadgetEmployee');
    setAppointments([]); go('home'); tell('ok', 'You have been logged out.');
  }



  async function saveProduct(e: FormEvent<HTMLFormElement>) {

    e.preventDefault(); if (!productForm) return; setBusy(true); const fd = new FormData(e.currentTarget);

    const body = { ...(productForm.id ? { product_id: productForm.id } : {}), product_name: fd.get('name'), brand: fd.get('brand'), description: fd.get('description'), price: Number(fd.get('price')), stock_quantity: Number(fd.get('stock')), category_id: Number(fd.get('categoryId')), image_url: fd.get('image') };

    try { await request(productForm.id ? 'update_product.php' : 'add_product.php', 'POST', body); await loadProducts(); setProductForm(null); tell('ok', productForm.id ? 'Product updated.' : 'Product added.'); }

    catch (err) { tell('error', err instanceof Error ? err.message : 'Could not save product.'); } finally { setBusy(false); }

  }

  async function removeProduct(p: Product) { if (!window.confirm(`Delete ${p.name}?`)) return; try { await request('delete_product.php', 'POST', { product_id: p.id }); await loadProducts(); tell('ok', 'Product deleted.'); } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not delete product.'); } }

  async function saveCategory(e: FormEvent<HTMLFormElement>) {

    e.preventDefault(); if (!categoryForm) return; setBusy(true); const fd = new FormData(e.currentTarget); const body = { ...(categoryForm.id ? { category_id: categoryForm.id } : {}), category_name: fd.get('name'), description: fd.get('description') };

    try { await request(categoryForm.id ? 'update_category.php' : 'add_category.php', 'POST', body); await loadCategories(); setCategoryForm(null); tell('ok', categoryForm.id ? 'Category updated.' : 'Category added.'); } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not save category.'); } finally { setBusy(false); }

  }

  async function removeCategory(c: Category) { if (!window.confirm(`Delete category ${c.name}?`)) return; try { await request('delete_category.php', 'POST', { category_id: c.id }); await loadCategories(); tell('ok', 'Category deleted.'); } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not delete category.'); } }

  async function book(e: FormEvent<HTMLFormElement>) {

    e.preventDefault(); if (!customer) { setAuthMode('login'); go('auth'); tell('error', 'Please log in before booking a repair.'); return; }

    const fd = new FormData(e.currentTarget);
    const issue = String(fd.get('issueDescription') || '').trim();
    if (!issue) { tell('error', 'Please describe the problem with your device.'); return; }
    const serviceDescription = `${String(fd.get('service') || '').trim()} — ${issue}`;
    setBusy(true);

    try { await request('create_appointment.php', 'POST', { customer_id: customer.id, device_type: fd.get('deviceType'), device_model: fd.get('deviceModel'), service_description: serviceDescription, appointment_date: fd.get('date'), appointment_time: fd.get('time') }); await loadCustomerAppointments(customer.id); go('appointments'); tell('ok', 'Repair appointment booked.'); }

    catch (e) { tell('error', e instanceof Error ? e.message : 'Could not book appointment.'); } finally { setBusy(false); }

  }

  async function cancelAppointment(a: Appointment) { if (!customer) return; if (!window.confirm('Cancel this pending appointment?')) return; try { await request('cancel_appointment.php', 'POST', { appointment_id: a.id, customer_id: customer.id }); await loadCustomerAppointments(customer.id); tell('ok', 'Appointment cancelled.'); } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not cancel appointment.'); } }

  async function changeStatus(a: Appointment, status: string) { if (status === 'Ready to Claim') { setClaimForm(a); return; } if (status === 'Completed' && !window.confirm('Confirm that the customer has collected this device?')) return; try { await request('update_appointment_status.php', 'POST', { appointment_id: a.id, status }); await loadAdminData(); tell('ok', 'Appointment status updated.'); } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not update appointment.'); } }

  async function saveClaim(e: FormEvent<HTMLFormElement>) {
    e.preventDefault(); if (!claimForm || claimSaving) return;
    const fd = new FormData(e.currentTarget);
    const notes = String(fd.get('repairNotes') || '').trim();
    const total = String(fd.get('repairTotal') || '').trim();
    if (!notes || !/^\d{1,8}(\.\d{1,2})?$/.test(total)) { tell('error', 'Enter repair notes and a valid total with at most two decimal places.'); return; }
    setClaimSaving(true);
    try {
      await request('update_appointment_status.php', 'POST', { appointment_id: claimForm.id, status: 'Ready to Claim', repair_notes: notes, repair_total: total });
      setClaimForm(null); await loadAdminData(); tell('ok', 'Device marked Ready to Claim. The customer can now view the repair summary.');
    } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not save repair summary.'); }
    finally { setClaimSaving(false); }
  }

  async function sendInquiry(e: FormEvent<HTMLFormElement>) { e.preventDefault(); if (!customer) { setAuthMode('login'); go('auth'); tell('error', 'Please log in before sending an inquiry.'); return; } const form = e.currentTarget; const fd = new FormData(form); try { await request('submit_inquiry.php', 'POST', { customer_id: customer.id, message: fd.get('message') }); form.reset(); tell('ok', 'Your inquiry was submitted.'); } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not send inquiry.'); } }



  async function loadProfile(id: number) {
    const d = await request<Raw>(`get_customer.php?id=${id}`);
    const c: Customer = { id: num(d.customer_id), firstName: d.first_name || '', lastName: d.last_name || '', email: d.email || '', contact: d.contact_number || '', address: d.address || '', street: d.street || '', barangay: d.barangay || '', city: d.city || '', postal_code: d.postal_code || '' };
    setCustomer(c);
    localStorage.setItem('gadgetCustomer', JSON.stringify(c));
  }
  const [editingProfile, setEditingProfile] = useState(false);
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileReady, setProfileReady] = useState(false);
  useEffect(() => {
    if (page !== 'profile' || !customer?.id) return;
    let active = true;
    setEditingProfile(false); setProfileLoading(true); setProfileReady(false);
    loadProfile(customer.id).then(() => { if (active) setProfileReady(true); }).catch(e => { if (active) tell('error', e.message); }).finally(() => { if (active) setProfileLoading(false); });
    return () => { active = false; };
  }, [page, customer?.id]);
  async function saveProfile(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (!customer || !profileReady) return;
    const fd = new FormData(e.currentTarget);
    const body = { customer_id: customer.id, first_name: String(fd.get('first_name') || '').trim(), last_name: String(fd.get('last_name') || '').trim(), contact_number: String(fd.get('contact_number') || '').trim(), street: String(fd.get('street') || '').trim(), barangay: String(fd.get('barangay') || '').trim(), city: String(fd.get('city') || '').trim(), postal_code: String(fd.get('postal_code') || '').trim() };
    if (!body.first_name || !body.last_name) { tell('error', 'First name and last name are required.'); return; }
    setBusy(true);
    try {
      await request('update_customer.php', 'POST', body);
      const c: Customer = { ...customer, firstName: body.first_name, lastName: body.last_name, contact: body.contact_number, street: body.street, barangay: body.barangay, city: body.city, postal_code: body.postal_code, address: [body.street, body.barangay, body.city, body.postal_code].filter(Boolean).join(', ') };
      setCustomer(c); localStorage.setItem('gadgetCustomer', JSON.stringify(c));
      setEditingProfile(false);
      tell('ok', 'Profile updated successfully.');
    } catch (e) { tell('error', e instanceof Error ? e.message : 'Could not update profile.'); }
    finally { setBusy(false); }
  }

  function openRecovery() {
    setResetStep('email'); setResetEmail(''); setResetRequestId(''); setResetToken(''); go('forgot');
  }
  async function recoverPassword(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const fd = new FormData(e.currentTarget); setBusy(true);
    try {
      if (resetStep === 'email') {
        const email = String(fd.get('email') || '').trim();
        const d = await request<Raw>('request_password_reset.php', 'POST', { email });
        if (!d.request_id) throw new Error('Could not start password recovery.');
        setResetEmail(email); setResetRequestId(d.request_id); setResetStep('code'); tell('ok', d.message);
      } else if (resetStep === 'code') {
        const d = await request<Raw>('verify_password_reset.php', 'POST', { request_id: resetRequestId, code: String(fd.get('code') || '').trim() });
        if (!d.reset_token) throw new Error('Could not verify reset code.');
        setResetToken(d.reset_token); setResetStep('password'); tell('ok', d.message);
      } else {
        const password = String(fd.get('password') || '');
        const confirm_password = String(fd.get('confirm_password') || '');
        if (password !== confirm_password) throw new Error('Passwords do not match.');
        if (new TextEncoder().encode(password).length > 72) throw new Error('Password is too long. Use at most 72 bytes.');
        const d = await request<Raw>('reset_password.php', 'POST', { reset_token: resetToken, password, confirm_password });
        setResetToken(''); setResetRequestId(''); setResetStep('email');
        sessionStorage.removeItem('gadgetCustomerToken'); localStorage.removeItem('gadgetCustomer'); setCustomer(null);
        setAuthMode('login'); go('auth'); tell('ok', d.message);
      }
    } catch (e) { tell('error', e instanceof Error ? e.message : 'Password recovery failed.'); }
    finally { setBusy(false); }
  }

  const isAdminView = !!employee && page.startsWith('admin');
  const navItems = isAdminView ? [['admin','Dashboard'],['admin-products','Products'],['admin-categories','Categories'],['admin-appointments','Appointments'],['admin-customers','Customers'],['admin-inquiries','Inquiries']] : [['home','Home'],['products','Products'],['services','Repair Services'],['appointments','My Appointments'],['contact','Contact']];

  const activeNav = (p: string) => page === p ? 'active' : '';

  const openProduct = (p: Product) => { setSelectedProduct(p); go('product'); setSelectedProduct(p); };



  return <div className="app-shell">

    {!isAdminView && <div className="top-strip"><div className="wrap top-inner"><span className="top-right">Mon–Fri 9:00 AM – 7:00 PM · Walk-ins welcome</span></div></div>}
    <header className={`site-header ${isAdminView ? 'admin-site-header' : ''}`}><div className="wrap nav-row">
      <button className="brand" onClick={() => go(isAdminView ? 'admin' : 'home')} aria-label={isAdminView ? "4RMTECH admin dashboard" : "4RMTECH home"}><img className="brand-image" src="/images/4rmtech.jpg" alt=""/><span className="brand-copy"><b>4RMTECH</b><small>{isAdminView ? 'ADMIN PORTAL' : 'GADGET SHOP & REPAIR SERVICES'}</small></span></button>
      <button className="mobile-toggle" aria-label="Toggle navigation" aria-expanded={showMobileNav} onClick={() => setShowMobileNav(!showMobileNav)}>{showMobileNav?<X/>:<Menu/>}</button>
      <nav aria-label="Main navigation" className={showMobileNav ? 'nav-links mobile-open' : 'nav-links'}>{navItems.map(([p,label])=><button key={p} className={activeNav(p)} onClick={()=>go(p)}>{label}{isAdminView && p==='admin-inquiries' && (unreadInquiries ?? 0)>0 && <span className="inquiry-badge">{unreadInquiries}</span>}</button>)}</nav>
      <div className="nav-actions">{isAdminView ? <><button className="admin-inquiry-indicator" onClick={()=>go('admin-inquiries')} aria-label={inquiryCheckFailed ? 'Could not check new inquiries. Open Inquiries to retry.' : `${unreadInquiries ?? 0} new inquiries`} title={inquiryCheckFailed ? 'Could not check inquiries. Check XAMPP.' : 'Customer inquiries'}><Mail size={21}/>{inquiryCheckFailed ? <span className="inquiry-badge">!</span> : (unreadInquiries ?? 0)>0 && <span className="inquiry-badge">{unreadInquiries}</span>}</button><span className="admin-account-label"><CircleUserRound size={19}/><span>{employee?.fullName}</span></span><button className="btn btn-outline btn-sm" onClick={logout}><LogOut size={17}/> Sign out</button></> : <><button className="btn btn-primary btn-sm nav-book" onClick={()=>go('book')}><CalendarDays size={19}/> Book a Repair</button>{customer ? <button className="account-chip" onClick={()=>go('profile')}><CircleUserRound size={18}/><span>{customer.firstName}</span></button> : employee ? <button className="btn btn-outline btn-sm" onClick={()=>go('admin')}>Dashboard</button> : <button className="btn btn-outline btn-sm" onClick={()=>{setAuthMode('login');go('auth')}}>Log in</button>}</>}</div>
    </div></header>

    {notice && <div className={`notice ${notice.type}`} role="status"><span>{notice.type==='ok'?<Check size={17}/>:<Activity size={17}/>}</span>{notice.text}<button onClick={()=>setNotice(null)}><X size={16}/></button></div>}



    {page==='home' && <>
      <section className="hero"><div className="wrap hero-grid"><div className="hero-copy"><div className="eyebrow"><ShieldCheck size={16}/> PROUDLY FILIPINO-OWNED · GADGET SHOP &amp; REPAIR SERVICES</div><h1>Gadgets you can<br/>inspect. Repairs you<br/>can trust.</h1><p>Browse our current stock of phones, laptops, audio gear and accessories online, then buy them in person at the shop. Need a fix? Book a repair slot and pay only after the job is done.</p><div className="hero-actions"><button className="btn btn-primary" onClick={()=>go('products')}>Browse Products <ArrowRight size={19}/></button><button className="btn btn-white" onClick={()=>go('book')}><CalendarDays size={19}/> Book a Repair</button></div><div className="hero-facts"><div><small>EXPLORE THE CATALOG</small><b>Shop onsite</b></div><div><small>REPAIR APPOINTMENTS</small><b>Book online</b></div><div><small>REPAIR PAYMENTS</small><b>Pay onsite</b></div></div></div><div className="workshop-visual"><img className="workshop-photo" src="/images/repair-workshop.jpg" alt="Technician working on a phone at a repair workbench"/><img className="workshop-logo" src="/images/4rmtech.jpg" alt="4RMTECH Gadget Shop and Repair Services"/></div></div></section>
      <section className="trust-row"><div className="wrap trust-grid"><div><Store/><span><b>Buy in store only</b><small>See the unit, test it, then pay at the counter.</small></span></div><div><BatteryCharging/><span><b>Repair made simple</b><small>Choose a convenient time to bring in your device.</small></span></div><div><MessageSquare/><span><b>Ask before you visit</b><small>Send an inquiry and our staff will reply with stock and pricing.</small></span></div></div></section>
      <section className="section wrap"><div className="section-heading"><div><span className="section-kicker">IN THE SHOP NOW</span><h2>Featured products</h2></div><button className="text-link" onClick={()=>go('products')}>View all products <ArrowRight size={18}/></button></div><ProductGrid products={products.slice(0,3)} onOpen={openProduct}/>{products.length===0 && <EmptyCatalog onRetry={()=>refreshCatalog()}/>}</section>
      <section className="services-section"><div className="wrap"><div className="section-heading"><div><span className="section-kicker">REPAIR CENTRE</span><h2>Main services</h2><p>Get help with your device from our repair team. Costs and turnaround are confirmed after assessment.</p></div></div><RepairCards/><div className="section-actions"><button className="btn btn-primary" onClick={()=>go('book')}><CalendarDays size={19}/> Book a Repair</button><button className="btn btn-outline" onClick={()=>go('services')}>See all services</button></div></div></section>
      <section className="section wrap booking-process"><div className="section-heading"><div><span className="section-kicker">FROM BOOKING TO PICKUP</span><h2>How a repair booking works</h2></div></div><div className="steps-grid">{[{icon:<ClipboardList/>,title:'Book online',text:'Pick a date and time, tell us the device and what went wrong.'},{icon:<CalendarDays/>,title:'We confirm',text:'Our staff review the request and confirm or suggest another slot.'},{icon:<Wrench/>,title:'Drop it off',text:'Visit the shop at your scheduled time and hand over the device.'},{icon:<Store/>,title:'Pay onsite',text:'Settle the repair fee at the counter once the work is finished.'}].map((step,i)=><article className="step-card" key={step.title}><span className="step-icon">{step.icon}</span><small>STEP {i+1}</small><h3>{step.title}</h3><p>{step.text}</p></article>)}</div></section>
      <section className="inquiry-banner"><div className="wrap inquiry-inner"><div><h2>Not sure what your device needs?</h2><p>Send us the details and our staff will get back to you. You can also call 0917 842 6690 during shop hours.</p></div><button className="btn btn-primary" onClick={()=>go('contact')}><MessageSquare size={19}/> Send Inquiry</button></div></section>
    </>}

    {page==='products' && <main className="wrap page-main"><PageTitle kicker="THE CATALOG" title="Explore products" subtitle="Find your next device or accessory."/><div className="catalog-tools"><label className="search-field"><Search size={18}/><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search products, brands..."/></label><label className="select-field"><Tag size={16}/><select value={categoryFilter} onChange={e=>setCategoryFilter(e.target.value)}><option value="all">All categories</option>{categories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select><ChevronDown size={15}/></label></div><p className="results-count">Showing {visibleProducts.length} product{visibleProducts.length===1?'':'s'}</p><ProductGrid products={visibleProducts} onOpen={openProduct}/>{!visibleProducts.length && <EmptyCatalog onRetry={()=>refreshCatalog()}/>}</main>}



    {page==='product' && selectedProduct && <main className="wrap page-main"><button className="back-link" onClick={()=>go('products')}>← Back to products</button><div className="detail-grid"><div className="detail-image"><ProductPhoto product={selectedProduct}/><span className="image-badge">4RMTECH PICK</span></div><div className="detail-copy"><span className="section-kicker">{selectedProduct.category || 'GADGETS'}</span><h1>{selectedProduct.name}</h1><p className="detail-brand">{selectedProduct.brand}</p><div className="detail-price">{money(selectedProduct.price)}</div><p>{selectedProduct.description || 'A dependable everyday device from 4RMTECH.'}</p><div className="stock-note"><span className={selectedProduct.stock>0?'stock-dot':'stock-dot out'}/>{selectedProduct.stock>0?`${selectedProduct.stock} in stock`:'Currently out of stock'}</div><button className="btn btn-primary" onClick={()=>go('book')}><Wrench size={17}/> Need device repair?</button><div className="detail-assurance"><ShieldCheck size={18}/> Questions about this product? <button onClick={()=>go('contact')}>Contact our team</button></div></div></div></main>}



    {page==='services' && <main className="wrap page-main"><PageTitle kicker="REPAIR CENTRE" title="Care for the tech you rely on" subtitle="Tell us about your device and schedule a repair appointment with our team."/><RepairCards/><div className="section-actions"><button className="btn btn-primary" onClick={()=>go('book')}><CalendarDays size={19}/> Book a Repair</button></div><div className="info-note"><ShieldCheck/> Final repair costs and turnaround are confirmed after the device has been assessed by the team.</div></main>}

    {page==='book' && <main className="wrap page-main narrow"><PageTitle kicker="BOOK A REPAIR" title="Let's fix that." subtitle="Share a few details about your device and choose a convenient time."/>{!customer && <div className="auth-callout"><CircleUserRound/> You need a customer account to book. <button onClick={()=>{setAuthMode('login');go('auth')}}>Sign in or register</button></div>}<form className="form-card" onSubmit={book}><div className="form-section-title"><span>01</span><div><b>Device details</b><small>Tell us what needs attention.</small></div></div><div className="form-grid"><Field label="Device type"><select name="deviceType" required defaultValue=""><option value="" disabled>Select device type</option><option>Smartphone</option><option>Laptop</option><option>Tablet</option><option>Desktop computer</option><option>Other</option></select></Field><Field label="Device model"><input name="deviceModel" required placeholder="e.g. Samsung Galaxy S24"/></Field><Field label="Service needed" wide><select name="service" required defaultValue=""><option value="" disabled>Choose a service</option><option>Screen replacement</option><option>Battery replacement</option><option>Charging issue</option><option>Software troubleshooting</option><option>Device diagnostics</option><option>Other repair</option></select></Field><Field label="Describe the issue" wide><textarea name="issueDescription" required maxLength={1000} rows={4} placeholder="e.g. The screen has a green line, or the device will not charge."/></Field><Field label="Preferred date"><input type="date" name="date" required min={new Date().toISOString().slice(0,10)}/></Field><Field label="Preferred time"><select name="time" required defaultValue=""><option value="" disabled>Select a time</option>{['09:00','10:00','11:00','13:00','14:00','15:00','16:00'].map(t=><option key={t} value={t}>{t}</option>)}</select></Field></div><button className="btn btn-primary btn-full" disabled={busy}>{busy?'Submitting...':'Confirm repair booking'} <ArrowRight size={17}/></button><p className="form-footnote"><ShieldCheck size={14}/> View and manage your booking in My Appointments.</p></form></main>}



    {page==='appointments' && <main className="wrap page-main"><PageTitle kicker="YOUR REPAIR BOOKINGS" title="My appointments" subtitle="Review your upcoming and previous repair appointments."/>{!customer ? <EmptyState title="Sign in to view appointments" text="Your bookings are connected to your customer account." action="Sign in" onAction={()=>{setAuthMode('login');go('auth')}}/> : <>{appointments.length ? <div className="appointment-list">{appointments.map(a=><article className="appointment-card" key={a.id}><div className="appointment-date"><CalendarDays/><b>{niceDate(a.date)}</b><small>{a.time}</small></div><div className="appointment-info"><h3>{a.model}</h3><p>{a.type} · {a.service}</p><span className={`status status-${a.status.toLowerCase().replace(/ /g, '-')}`}>{a.status}</span><RepairSummary appointment={a}/></div>{a.status.toLowerCase()==='pending' && <button className="btn btn-danger-outline btn-sm" onClick={()=>cancelAppointment(a)}>Cancel</button>}</article>)}</div> : <EmptyState title="No appointments yet" text="When you book a repair, it will appear here." action="Book a repair" onAction={()=>go('book')}/>}</>}</main>}



    {page==='profile' && <main className="wrap page-main narrow"><PageTitle kicker="MY ACCOUNT" title="My Profile" subtitle="View and update your personal details."/>{!customer ? <EmptyState title="Sign in to view your profile" text="Your profile is linked to your customer account." action="Sign in" onAction={()=>{setAuthMode('login');go('auth')}}/> : profileLoading ? <div className="form-card" role="status">Loading your profile...</div> : !profileReady ? <div className="form-card"><p>Could not load your profile. Reopen My Profile to try again.</p><button className="btn btn-outline" onClick={()=>go('home')}>Back to home</button></div> : !editingProfile ? <section className="form-card"><div className="form-grid">{[['First name', customer.firstName], ['Last name', customer.lastName], ['Email address', customer.email], ['Contact number', customer.contact], ['Street', customer.street], ['Barangay', customer.barangay], ['City', customer.city], ['Postal code', customer.postal_code]].map(([label, value]) => <div className="field" key={label}><span>{label}</span><p style={{ margin: '8px 0 16px', overflowWrap: 'anywhere' }}>{value || '—'}</p></div>)}</div><button type="button" className="btn btn-primary btn-full" onClick={()=>{setNotice(null);setEditingProfile(true)}}>Edit Profile</button></section> : <form className="form-card" onSubmit={saveProfile}><div className="form-grid"><Field label="First name"><input name="first_name" required maxLength={100} autoComplete="given-name" defaultValue={customer.firstName}/></Field><Field label="Last name"><input name="last_name" required maxLength={100} autoComplete="family-name" defaultValue={customer.lastName}/></Field><Field label="Email address" wide><input type="email" value={customer.email} readOnly/></Field><Field label="Contact number" wide><input name="contact_number" type="tel" autoComplete="tel" maxLength={30} defaultValue={customer.contact}/></Field><Field label="Street" wide><input name="street" autoComplete="address-line1" defaultValue={customer.street || ''}/></Field><Field label="Barangay"><input name="barangay" defaultValue={customer.barangay || ''}/></Field><Field label="City"><input name="city" autoComplete="address-level2" defaultValue={customer.city || ''}/></Field><Field label="Postal code"><input name="postal_code" autoComplete="postal-code" defaultValue={customer.postal_code || ''}/></Field></div><button className="btn btn-primary btn-full" disabled={busy}>{busy?'Saving...':'Save changes'}</button><button type="button" className="btn btn-outline btn-full" style={{marginTop: 12}} disabled={busy} onClick={()=>{setEditingProfile(false);setNotice(null)}}>Cancel</button></form>}</main>}

    {page==='contact' && <main className="wrap page-main inquiry-page"><PageTitle kicker="CONTACT 4RMTECH" title="Inquiries & frequently asked questions" subtitle="Find a quick answer below, or send our team a message about your device."/><div className="inquiry-layout"><section className="faq-panel" aria-labelledby="faq-title"><div className="faq-heading"><span className="section-kicker">QUICK ANSWERS</span><h2 id="faq-title">Frequently Asked Questions</h2><p>Helpful information before you visit or book a repair.</p></div><div className="faq-list">{[
      ['Can I buy a gadget through the website?', 'You can browse products, prices and availability online. To buy, visit the shop so you can inspect the device and pay at the counter.'],
      ['How do I book a repair?', 'Sign in to your customer account, select Book a Repair, enter your device details, and choose a date and time. Submit your request, then check My Appointments for its status.'],
      ['How much will my repair cost, and how long will it take?', 'The cost and turnaround depend on your device and the problem. Our team will confirm them after assessing the device. Send an inquiry with the model and symptoms for guidance.'],
      ['When do I pay for a repair?', 'Repair payments are made onsite at the shop after the work is finished.'],
      ['How can I check or cancel my appointment?', 'Sign in and open My Appointments to see your bookings and their status. Pending appointments have a Cancel button. For other changes, send our team an inquiry.'],
      ['What should I do if I forgot my password?', 'On the customer sign-in page, select Forgot password. Enter your registered email, verify the emailed code, and choose a new password. Check your spam folder if the email does not appear.'],
      ['Do I need an account to send an inquiry?', 'Yes. Sign in to your customer account before sending a message. Include your device model and a clear description of the issue so our team can help.']
    ].map(([question,answer])=><details className="faq-item" key={question}><summary>{question}<ChevronDown size={19} aria-hidden="true"/></summary><p>{answer}</p></details>)}</div></section><section className="inquiry-message-panel" aria-label="Send an inquiry"><form className="form-card" onSubmit={sendInquiry}><div className="contact-note"><Mail/><span><b>Send an inquiry</b><small>{customer?`Signed in as ${customer.email}`:'Please sign in before sending an inquiry.'}</small></span></div><Field label="Your message"><textarea name="message" required maxLength={1000} rows={5} placeholder="Tell us how we can help..."/></Field><button className="btn btn-primary btn-full">Send message <ArrowRight size={17}/></button></form></section></div></main>}

    {page==='forgot' && <main className="wrap page-main narrow"><PageTitle kicker="ACCOUNT RECOVERY" title="Reset your password" subtitle={resetStep==='email'?'Enter your customer account email.':resetStep==='code'?`Enter the code sent to ${resetEmail}.`:'Choose a new password for your customer account.'}/><form className="form-card" key={resetStep} onSubmit={recoverPassword}>{resetStep==='email' && <Field label="Email address"><input type="email" name="email" required autoComplete="email" maxLength={254} defaultValue={resetEmail}/></Field>}{resetStep==='code' && <><Field label="Six-digit reset code"><input name="code" required inputMode="numeric" pattern="[0-9]{6}" maxLength={6} autoComplete="one-time-code" placeholder="123456"/></Field><p className="form-footnote">The code expires 10 minutes after your request. Check your spam folder too.</p></>}{resetStep==='password' && <><Field label="New password"><input name="password" type="password" minLength={6} required autoComplete="new-password"/></Field><Field label="Confirm new password"><input name="confirm_password" type="password" minLength={6} required autoComplete="new-password"/></Field><p className="form-footnote">Use at least 6 characters. You will sign in again after resetting.</p></>}<button className="btn btn-primary btn-full" disabled={busy}>{busy?'Please wait...':resetStep==='email'?'Send reset code':resetStep==='code'?'Verify code':'Save new password'}</button>{resetStep!=='email' && <button type="button" className="btn btn-outline btn-full" style={{marginTop:12}} disabled={busy} onClick={()=>{setResetStep('email');setResetToken('');setResetRequestId('');setNotice(null)}}>Request another code</button>}<button type="button" className="back-link" style={{marginTop:16}} disabled={busy} onClick={()=>{setResetToken('');setResetRequestId('');setAuthMode('login');go('auth')}}>Back to sign in</button></form></main>}

    {page==='auth' && <main className="auth-page"><div className="auth-aside"><div className="auth-aside-content"><img className="auth-brand-image" src="/images/4rmtech.jpg" alt="4RMTECH"/><span className="section-kicker light">WELCOME TO 4RMTECH</span><h1>Technology made<br/>more convenient.</h1><p>Shop your essentials, manage your profile, and keep your repairs on track.</p><div className="auth-benefit"><Check/> One account for your shop experience</div><div className="auth-benefit"><Check/> Keep track of your repair appointments</div></div></div><div className="auth-form-side"><form className="auth-form" onSubmit={submitAuth}><button type="button" className="back-link" onClick={()=>go('home')}>← Back to shop</button><span className="section-kicker">{authMode==='admin'?'TEAM ACCESS':'YOUR 4RMTECH ACCOUNT'}</span><h2>{authMode==='register'?'Create your account':authMode==='admin'?'Admin sign in':'Welcome back'}</h2><p>{authMode==='register'?'Join us to manage your repairs and account.':authMode==='admin'?'Sign in with your existing employee credentials.':'Sign in to book and manage your repairs.'}</p>{authMode==='register'&&<div className="form-grid two"><Field label="First name"><input name="firstName" required autoComplete="given-name"/></Field><Field label="Last name"><input name="lastName" required autoComplete="family-name"/></Field></div>}{authMode==='admin'?<Field label="Username"><input name="username" required autoComplete="username"/></Field>:<Field label="Email address"><input type="email" name="email" required autoComplete="email"/></Field>}{authMode==='login' && <button type="button" className="back-link" onClick={openRecovery}>Forgot password?</button>}<Field label="Password"><input type="password" name="password" required minLength={6} autoComplete={authMode==='register'?'new-password':'current-password'}/></Field><button className="btn btn-primary btn-full" disabled={busy}>{busy?'Please wait...':authMode==='register'?'Create account':authMode==='admin'?'Sign in to admin':'Sign in'} <ArrowRight size={17}/></button>{authMode!=='admin'&&<p className="auth-switch">{authMode==='register'?'Already have an account?':'New to 4RMTECH?'} <button type="button" onClick={()=>setAuthMode(authMode==='register'?'login':'register')}>{authMode==='register'?'Sign in':'Create an account'}</button></p>}{authMode==='admin'&&<p className="auth-switch"><button type="button" onClick={()=>setAuthMode('login')}>Customer sign in</button></p>}{authMode==='login'&&<p className="auth-switch admin-link"><button type="button" onClick={()=>setAuthMode('admin')}>Employee / admin access</button></p>}<p className="secure-note"><ShieldCheck size={15}/> Your account details are kept secure.</p></form></div></main>}



    {page.startsWith('admin') && <main className="wrap page-main admin-main">{!employee ? <EmptyState title="Admin sign in required" text="Use the employee account already stored in your database." action="Admin sign in" onAction={()=>{setAuthMode('admin');go('auth')}}/> : <><div className="admin-welcome"><div><span className="section-kicker">SHOP CONTROL CENTER</span><h1>Welcome back, {employee.fullName.split(' ')[0]}</h1><p>Manage your live catalog and repair queue.</p></div><button className="btn btn-outline btn-sm" onClick={()=>loadAdminData()}><Activity size={15}/> Refresh data</button></div><div className="admin-stats"><Stat icon={<Package/>} label="Products" value={products.length}/><Stat icon={<Tag/>} label="Categories" value={categories.length}/><Stat icon={<CalendarDays/>} label="Appointments" value={appointments.length}/><Stat icon={<CircleUserRound/>} label="Customers" value={customers.length}/></div><div className="admin-tabs">{[['admin','Overview'],['admin-products','Products'],['admin-categories','Categories'],['admin-appointments','Appointments'],['admin-customers','Customers'],['admin-inquiries','Inquiries']].map(([p,l])=><button key={p} className={page===p?'selected':''} onClick={()=>go(p)}>{l}{p==='admin-inquiries' && (unreadInquiries ?? 0)>0 && <span className="inquiry-badge">{unreadInquiries} new</span>}</button>)}</div>

      {page==='admin'&&<div className="admin-overview-grid"><div className="panel"><h3>Appointments needing attention</h3>{appointments.filter(a=>a.status.toLowerCase()==='pending').slice(0,5).map(a=><div className="mini-row" key={a.id}><span className="mini-icon"><CalendarDays/></span><span><b>{a.firstName} {a.lastName} · {a.model}</b><small>{niceDate(a.date)} · {a.time}</small></span><span className="status status-pending">Pending</span></div>)}{!appointments.some(a=>a.status.toLowerCase()==='pending')&&<p className="muted">No pending appointments.</p>}<button className="text-link" onClick={()=>go('admin-appointments')}>Manage appointments <ArrowRight size={15}/></button></div><div className="panel"><h3>Low-stock products</h3>{products.filter(p=>p.stock<=5).slice(0,6).map(p=><div className="mini-row" key={p.id}><span className="mini-icon"><Package/></span><span><b>{p.name}</b><small>{p.brand}</small></span><strong className={p.stock===0?'danger-text':'warning-text'}>{p.stock} left</strong></div>)}{!products.some(p=>p.stock<=5)&&<p className="muted">Stock levels look good.</p>}<button className="text-link" onClick={()=>go('admin-products')}>Manage products <ArrowRight size={15}/></button></div></div>}

      {page==='admin-products'&&<div className="panel"><div className="panel-head"><div><h3>Product inventory</h3><p>Changes are saved to PostgreSQL through the PHP API.</p></div><button className="btn btn-primary btn-sm" onClick={()=>setProductForm({})}><Plus size={16}/> Add product</button></div><div className="table-wrap"><table><thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Stock</th><th>Actions</th></tr></thead><tbody>{products.map(p=><tr key={p.id}><td><div className="table-product">{p.image?<img src={p.image} alt=""/>:<span className="table-placeholder"><Package/></span>}<span><b>{p.name}</b><small>{p.brand}</small></span></div></td><td>{p.category}</td><td>{money(p.price)}</td><td>{p.stock}</td><td><div className="table-actions"><button className="btn btn-light btn-xs" onClick={()=>setProductForm(p)}>Edit</button><button className="icon-btn danger-icon" title="Delete product" onClick={()=>removeProduct(p)}><Trash2 size={16}/></button></div></td></tr>)}</tbody></table></div></div>}

      {page==='admin-categories'&&<div className="panel"><div className="panel-head"><div><h3>Categories</h3><p>Organize the live product catalog.</p></div><button className="btn btn-primary btn-sm" onClick={()=>setCategoryForm({})}><Plus size={16}/> Add category</button></div><div className="category-admin-list">{categories.map(c=><div className="category-admin-row" key={c.id}><span className="category-icon"><Tag/></span><span className="grow"><b>{c.name}</b><small>{c.description||'No description'} · {c.count??0} products</small></span><button className="btn btn-light btn-xs" onClick={()=>setCategoryForm(c)}>Edit</button><button className="icon-btn danger-icon" title="Delete category" onClick={()=>removeCategory(c)}><Trash2 size={16}/></button></div>)}</div></div>}

      {page==='admin-appointments'&&<div className="panel"><div className="panel-head"><div><h3>Repair appointments</h3><p>Review requests and update repair status.</p></div></div><div className="table-wrap"><table><thead><tr><th>Customer</th><th>Device / service</th><th>Date</th><th>Status</th><th>Update status</th></tr></thead><tbody>{appointments.map(a=><tr key={a.id}><td><b>{a.firstName} {a.lastName}</b><small>{a.email}</small></td><td><b>{a.model}</b><small>{a.type} · {a.service}</small><RepairSummary appointment={a}/></td><td>{niceDate(a.date)}<small>{a.time}</small></td><td><span className={`status status-${a.status.toLowerCase().replace(/ /g, '-')}`}>{a.status}</span></td><td><select
  className="status-select"
  value={a.status}
  disabled={!(nextStatuses[a.status] || []).length}
  onChange={e => changeStatus(a, e.target.value)}
>
  <option value={a.status}>{a.status}</option>
  {(nextStatuses[a.status] || []).map(status => (
    <option key={status} value={status}>{status}</option>
  ))}
</select></td></tr>)}</tbody></table></div></div>}

      {page==='admin-customers'&&<div className="panel"><div className="panel-head"><div><h3>Registered customers</h3><p>Customer records from PostgreSQL.</p></div></div><div className="table-wrap"><table><thead><tr><th>Customer</th><th>Email</th><th>Contact</th><th>Location</th></tr></thead><tbody>{customers.map(c=><tr key={c.customer_id}><td><b>{c.first_name} {c.last_name}</b><small>Customer #{c.customer_id}</small></td><td>{c.email}</td><td>{c.contact_number||'—'}</td><td>{[c.street,c.barangay,c.city,c.postal_code].filter(Boolean).join(', ')||'—'}</td></tr>)}</tbody></table></div></div>}

      {page==='admin-inquiries'&&<div className="panel"><div className="panel-head"><div><h3>Customer inquiries</h3><p>Messages from Android and the website. Opening this list clears the new-inquiry badge for the admin team.</p></div></div>{inquiries.map(i=><article className="inquiry-row" key={i.id}><div className="inquiry-avatar"><Mail/></div><div><b>{i.name||'Customer'} <small>· {i.email}</small></b><p>{i.message}</p><small>{i.date?new Date(i.date).toLocaleString():'Date not available'}</small></div></article>)}{!inquiries.length&&<p className="muted padded">No inquiries found.</p>}</div>}

    </>}</main>}



    {isAdminView ? <footer className="admin-footer"><div className="wrap"><span>© {new Date().getFullYear()} 4RMTECH · Admin Portal</span><button className="text-link" onClick={()=>go('home')}>View storefront <ArrowRight size={16}/></button></div></footer> : <>    <footer className="site-footer"><div className="wrap footer-main"><div className="footer-brand"><button className="brand footer-logo" onClick={()=>go('home')}><img className="brand-image" src="/images/4rmtech.jpg" alt=""/><span className="brand-copy"><b>4RMTECH</b><small>GADGET SHOP &amp; REPAIR SERVICES</small></span></button><p>A neighborhood gadget shop and repair centre. Browse our stock online, then drop by the store to buy. Repairs are booked online and paid for onsite once the work is done.</p></div><div><b>EXPLORE</b><button onClick={()=>go('products')}>Browse Products</button><button onClick={()=>go('services')}>Repair Services</button><button onClick={()=>go('book')}>Book a Repair</button><button onClick={()=>go('contact')}>Send Inquiry</button><button onClick={()=>{setAuthMode('admin');go('auth')}}>Employee Login</button>{employee && <button onClick={logout}>Admin sign out</button>}</div><div className="visit-us"><b>VISIT US</b><p><MapPin size={20}/><span>Unit 4, Rizal Avenue, Poblacion District, Philippines</span></p><a href="tel:09178426690"><Phone size={20}/> 0917 842 6690</a><a href="mailto:support@4rmtech.ph"><Mail size={20}/> support@4rmtech.ph</a><small>Mon–Fri · 9:00 AM – 7:00 PM</small></div></div><div className="wrap footer-bottom"><span>© {new Date().getFullYear()} 4RMTECH Gadget Shop &amp; Repair Services</span><div>{customer && <button onClick={()=>go('profile')}>My Profile</button>}<button onClick={()=>go('appointments')}>My Appointments</button>{customer?<button onClick={logout}>Sign out</button>:<button onClick={()=>{setAuthMode('login');go('auth')}}>Customer Login</button>}</div></div></footer></>}

    {claimForm && <Modal title="Ready to Claim · Repair summary" onClose={()=>{if (!claimSaving) setClaimForm(null)}}><form className="modal-form" onSubmit={saveClaim}><p>{claimForm.model} · {claimForm.firstName} {claimForm.lastName}</p><Field label="Work done / parts and price breakdown"><textarea name="repairNotes" required maxLength={3000} rows={6} placeholder={'Replaced LCD screen — ₱1,500\nLabor — ₱300\nTested display and touch response.'}/></Field><Field label="Total amount (PHP)"><input name="repairTotal" type="number" required min="0" max="99999999.99" step="0.01" placeholder="1800.00"/></Field><p className="form-footnote">The customer can view these details. Enter 0 for a free repair. This summary does not mark payment as received.</p><button className="btn btn-primary btn-full" disabled={claimSaving}>{claimSaving?'Saving...':'Save & mark Ready to Claim'}</button></form></Modal>}

    {productForm && <Modal title={productForm.id?'Edit product':'Add a product'} onClose={()=>setProductForm(null)}><form className="modal-form" onSubmit={saveProduct}><Field label="Product name"><input name="name" required defaultValue={productForm.name||''}/></Field><Field label="Brand"><input name="brand" required defaultValue={productForm.brand||''}/></Field><Field label="Description"><textarea name="description" required rows={3} defaultValue={productForm.description||''}/></Field><div className="form-grid two"><Field label="Price (PHP)"><input type="number" name="price" min="0" step="0.01" required defaultValue={productForm.price??''}/></Field><Field label="Stock quantity"><input type="number" name="stock" min="0" step="1" required defaultValue={productForm.stock??0}/></Field></div><Field label="Category"><select name="categoryId" required defaultValue={productForm.categoryId||categories[0]?.id||''}>{categories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></Field><Field label="Image URL"><input name="image" type="url" placeholder="https://..." defaultValue={productForm.image||''}/></Field><button className="btn btn-primary btn-full" disabled={busy}>{busy?'Saving...':'Save product'}</button></form></Modal>}

    {categoryForm && <Modal title={categoryForm.id?'Edit category':'Add a category'} onClose={()=>setCategoryForm(null)}><form className="modal-form" onSubmit={saveCategory}><Field label="Category name"><input name="name" required maxLength={100} defaultValue={categoryForm.name||''}/></Field><Field label="Description"><textarea name="description" rows={3} defaultValue={categoryForm.description||''}/></Field><button className="btn btn-primary btn-full" disabled={busy}>{busy?'Saving...':'Save category'}</button></form></Modal>}

  </div>;

}



function fallbackProductImage(p: Product) {
  const type = `${p.category} ${p.name}`.toLowerCase();
  if (/laptop|notebook|tablet|computer|macbook/.test(type)) return '/images/laptop.jpg';
  if (/headphone|audio|earbud|speaker|headset/.test(type)) return '/images/audio.jpg';
  if (/phone|smartphone|iphone/.test(type)) return '/images/smartphones.jpg';
  return '/images/4rmtech.jpg';
}
function ProductPhoto({product, lazy=false}:{product:Product;lazy?:boolean}) {
  const fallback = fallbackProductImage(product);
  return <img src={product.image || fallback} alt={product.name} loading={lazy?'lazy':'eager'} onError={e=>{if(e.currentTarget.getAttribute('src')!==fallback)e.currentTarget.src=fallback;}}/>;
}
function ProductGrid({ products, onOpen }: { products: Product[]; onOpen: (p: Product)=>void }) { return <div className="product-grid">{products.map(p=><article className="product-card" key={p.id}><button className="product-image" aria-label={`View ${p.name}`} onClick={()=>onOpen(p)}><ProductPhoto product={p} lazy/></button><div className="product-info"><span className="product-brand">{p.brand||'4RMTECH'} · {p.category||'GADGETS'}</span><h3><button onClick={()=>onOpen(p)}>{p.name}</button></h3><p>{p.description||'Visit the shop to inspect this product and ask our team for more details.'}</p><div className="product-summary"><div><span>BRAND</span><b>{p.brand||'4RMTECH'}</b></div><div><span>CATEGORY</span><b>{p.category||'Gadgets'}</b></div><div><span>AVAILABILITY</span><b>{p.stock>0?`${p.stock} available`:'Currently unavailable'}</b></div><div><span>PURCHASE</span><b>Visit us in store</b></div></div><div className="product-bottom"><strong>{money(p.price)}</strong><span className={p.stock>5?'in-stock':p.stock>0?'low-stock':'out-stock'}>{p.stock>5?'In stock':p.stock>0?`${p.stock} left`:'Out of stock'}</span></div><button className="text-link product-details-link" onClick={()=>onOpen(p)}>View details <ArrowRight size={18}/></button></div></article>)}</div>; }
function RepairCards() { return <div className="service-grid reference-services">{[
  {title:'Screen & Display Replacement',text:'Cracked glass, dead pixels, or an unresponsive touch panel — get your display assessed by our team.',label:'SCREEN & DISPLAY'},
  {title:'Battery Replacement',text:'Health checks and battery replacements for phones, tablets and laptops.',label:'BATTERY & POWER'},
  {title:'Charging Port Repair',text:'Assessment and repair for devices that no longer charge reliably.',label:'CHARGING ISSUES'},
  {title:'Liquid Damage Treatment',text:'Device inspection, cleaning and component diagnosis after spills.',label:'LIQUID DAMAGE'},
  {title:'Software & OS Troubleshooting',text:'Help with boot loops, slow performance, software issues and operating system problems.',label:'SOFTWARE SUPPORT'},
  {title:'Laptop Cleaning & Upgrade',text:'Ask our team about cleaning, thermal paste renewal, and compatible RAM or SSD upgrades.',label:'LAPTOP CARE'}
].map(s=><article className="service-card" key={s.title}><h3>{s.title}</h3><p>{s.text}</p><span className="service-label">{s.label}</span></article>)}</div>; }

function PageTitle({kicker,title,subtitle}:{kicker:string;title:string;subtitle:string}) { return <div className="page-title"><span className="section-kicker">{kicker}</span><h1>{title}</h1><p>{subtitle}</p></div>; }

function Field({label,children,wide}:{label:string;children:ReactNode;wide?:boolean}) { return <label className={`field ${wide?'field-wide':''}`}><span>{label}</span>{children}</label>; }

function EmptyCatalog({onRetry}:{onRetry:()=>void}) { return <div className="empty-state"><span className="empty-icon"><Package/></span><h3>No products to show</h3><p>When your API is available, your PostgreSQL catalog will appear here.</p><button className="btn btn-outline" onClick={onRetry}>Retry connection</button></div>; }

function EmptyState({title,text,action,onAction}:{title:string;text:string;action:string;onAction:()=>void}) { return <div className="empty-state"><span className="empty-icon"><CalendarDays/></span><h3>{title}</h3><p>{text}</p><button className="btn btn-primary" onClick={onAction}>{action} <ArrowRight size={16}/></button></div>; }

function Stat({icon,label,value}:{icon:ReactNode;label:string;value:number}) { return <div className="stat-card"><span>{icon}</span><small>{label}</small><b>{value}</b></div>; }

function Modal({title,children,onClose}:{title:string;children:ReactNode;onClose:()=>void}) { return <div className="modal-backdrop" role="presentation" onMouseDown={e=>e.target===e.currentTarget&&onClose()}><section className="modal"><div className="modal-head"><h2>{title}</h2><button className="icon-btn" onClick={onClose}><X/></button></div>{children}</section></div>; }

function RepairSummary({ appointment: a }: { appointment: Appointment }) {
  if (!a.repairNotes) return null;
  return <section className="repair-summary" aria-label="Repair summary"><b>Repair summary</b>{a.status === 'Ready to Claim' && <p className="claim-message">Your device is ready for collection.</p>}<p className="repair-notes">{a.repairNotes}</p>{a.repairTotal != null && <strong>Total: {money(a.repairTotal)}</strong>}<small>Payment is handled at the shop. This is a repair summary.</small></section>;
}
