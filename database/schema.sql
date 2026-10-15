--
-- PostgreSQL database dump
--

\restrict YiStJfqSbiDH9X8ZPAJhr0PGpE3iP8seq2os9rOZSx6wVPV4kpw5dNnOpOq9Ah3

-- Dumped from database version 18.6
-- Dumped by pg_dump version 18.6

-- Started on 2026-10-15 12:29:00

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 239 (class 1259 OID 41137)
-- Name: Products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Products" (
    "ProductsID" integer NOT NULL,
    categoryid integer,
    name character varying(255) NOT NULL,
    price numeric(10,2) NOT NULL,
    inventory integer DEFAULT 0
);


ALTER TABLE public."Products" OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 41136)
-- Name: Products_ProductsID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Products_ProductsID_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Products_ProductsID_seq" OWNER TO postgres;

--
-- TOC entry 5158 (class 0 OID 0)
-- Dependencies: 238
-- Name: Products_ProductsID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Products_ProductsID_seq" OWNED BY public."Products"."ProductsID";


--
-- TOC entry 237 (class 1259 OID 41123)
-- Name: User; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."User" (
    "UserID" integer NOT NULL,
    address text,
    "birthDate" date,
    "createdAt" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    email character varying(255) NOT NULL,
    firstname character varying(100),
    lastname character varying(100)
);


ALTER TABLE public."User" OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 41122)
-- Name: User_UserID_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."User_UserID_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."User_UserID_seq" OWNER TO postgres;

--
-- TOC entry 5159 (class 0 OID 0)
-- Dependencies: 236
-- Name: User_UserID_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."User_UserID_seq" OWNED BY public."User"."UserID";


--
-- TOC entry 240 (class 1259 OID 49295)
-- Name: api_auth_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.api_auth_tokens (
    token_hash character(64) NOT NULL,
    customer_id integer,
    employee_id integer,
    expires_at timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT api_auth_tokens_check CHECK (((customer_id IS NOT NULL) <> (employee_id IS NOT NULL)))
);


ALTER TABLE public.api_auth_tokens OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 49350)
-- Name: api_mail_outbox; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.api_mail_outbox (
    mail_id bigint NOT NULL,
    request_id character(64),
    payload text,
    status character varying(12) DEFAULT 'pending'::character varying NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    available_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lease_until timestamp with time zone,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT api_mail_outbox_status_check CHECK (((status)::text = ANY ((ARRAY['pending'::character varying, 'sending'::character varying, 'sent'::character varying, 'failed'::character varying, 'cancelled'::character varying])::text[])))
);


ALTER TABLE public.api_mail_outbox OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 49349)
-- Name: api_mail_outbox_mail_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.api_mail_outbox_mail_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.api_mail_outbox_mail_id_seq OWNER TO postgres;

--
-- TOC entry 5160 (class 0 OID 0)
-- Dependencies: 243
-- Name: api_mail_outbox_mail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.api_mail_outbox_mail_id_seq OWNED BY public.api_mail_outbox.mail_id;


--
-- TOC entry 241 (class 1259 OID 49316)
-- Name: api_password_resets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.api_password_resets (
    request_id character(64) NOT NULL,
    customer_id integer,
    code_hash text NOT NULL,
    attempts integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    verified_at timestamp with time zone,
    reset_token_hash character(64),
    used_at timestamp with time zone,
    CONSTRAINT api_password_resets_attempts_check CHECK (((attempts >= 0) AND (attempts <= 5)))
);


ALTER TABLE public.api_password_resets OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 49339)
-- Name: api_reset_limits; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.api_reset_limits (
    limit_key character(64) NOT NULL,
    window_start timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    hits integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.api_reset_limits OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 41036)
-- Name: appointments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.appointments (
    appointment_id integer NOT NULL,
    customer_id integer NOT NULL,
    employee_id integer,
    device_id integer NOT NULL,
    appointment_date date NOT NULL,
    appointment_time time without time zone NOT NULL,
    service_description text NOT NULL,
    status character varying(30) DEFAULT 'Pending'::character varying NOT NULL,
    repair_notes text,
    repair_total numeric(10,2),
    ready_to_claim_at timestamp with time zone,
    CONSTRAINT appointments_repair_total_nonnegative CHECK (((repair_total IS NULL) OR (repair_total >= (0)::numeric)))
);


ALTER TABLE public.appointments OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 41035)
-- Name: appointments_appointment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.appointments_appointment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.appointments_appointment_id_seq OWNER TO postgres;

--
-- TOC entry 5161 (class 0 OID 0)
-- Dependencies: 229
-- Name: appointments_appointment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.appointments_appointment_id_seq OWNED BY public.appointments.appointment_id;


--
-- TOC entry 220 (class 1259 OID 40962)
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    category_id integer NOT NULL,
    category_name character varying(100) NOT NULL,
    description text
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 40961)
-- Name: categories_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categories_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categories_category_id_seq OWNER TO postgres;

--
-- TOC entry 5162 (class 0 OID 0)
-- Dependencies: 219
-- Name: categories_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categories_category_id_seq OWNED BY public.categories.category_id;


--
-- TOC entry 224 (class 1259 OID 40994)
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    customer_id integer NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    contact_number character varying(20),
    email character varying(150) NOT NULL,
    password_hash character varying(255) NOT NULL,
    street character varying(150),
    barangay character varying(100),
    city character varying(100),
    postal_code character varying(10)
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 40993)
-- Name: customers_customer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.customers_customer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.customers_customer_id_seq OWNER TO postgres;

--
-- TOC entry 5163 (class 0 OID 0)
-- Dependencies: 223
-- Name: customers_customer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.customers_customer_id_seq OWNED BY public.customers.customer_id;


--
-- TOC entry 226 (class 1259 OID 41010)
-- Name: devices; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.devices (
    device_id integer NOT NULL,
    device_model character varying(150) NOT NULL,
    device_type character varying(100) NOT NULL
);


ALTER TABLE public.devices OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 41009)
-- Name: devices_device_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.devices_device_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.devices_device_id_seq OWNER TO postgres;

--
-- TOC entry 5164 (class 0 OID 0)
-- Dependencies: 225
-- Name: devices_device_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.devices_device_id_seq OWNED BY public.devices.device_id;


--
-- TOC entry 228 (class 1259 OID 41020)
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employees (
    employee_id integer NOT NULL,
    full_name character varying(150) NOT NULL,
    username character varying(100) NOT NULL,
    contact_number character varying(20),
    role character varying(50) NOT NULL,
    password_hash character varying(255) NOT NULL
);


ALTER TABLE public.employees OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 41019)
-- Name: employees_employee_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.employees_employee_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.employees_employee_id_seq OWNER TO postgres;

--
-- TOC entry 5165 (class 0 OID 0)
-- Dependencies: 227
-- Name: employees_employee_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.employees_employee_id_seq OWNED BY public.employees.employee_id;


--
-- TOC entry 232 (class 1259 OID 41068)
-- Name: inquiries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inquiries (
    inquiry_id integer NOT NULL,
    customer_id integer NOT NULL,
    message text NOT NULL,
    date_submitted timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    admin_read_at timestamp with time zone
);


ALTER TABLE public.inquiries OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 41067)
-- Name: inquiries_inquiry_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.inquiries_inquiry_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.inquiries_inquiry_id_seq OWNER TO postgres;

--
-- TOC entry 5166 (class 0 OID 0)
-- Dependencies: 231
-- Name: inquiries_inquiry_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inquiries_inquiry_id_seq OWNED BY public.inquiries.inquiry_id;


--
-- TOC entry 233 (class 1259 OID 41086)
-- Name: inquiry_products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inquiry_products (
    inquiry_id integer NOT NULL,
    product_id integer NOT NULL
);


ALTER TABLE public.inquiry_products OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 41104)
-- Name: password_reset_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.password_reset_tokens (
    reset_id integer NOT NULL,
    customer_id integer NOT NULL,
    code_hash text NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    used_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.password_reset_tokens OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 41103)
-- Name: password_reset_tokens_reset_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.password_reset_tokens_reset_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.password_reset_tokens_reset_id_seq OWNER TO postgres;

--
-- TOC entry 5167 (class 0 OID 0)
-- Dependencies: 234
-- Name: password_reset_tokens_reset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.password_reset_tokens_reset_id_seq OWNED BY public.password_reset_tokens.reset_id;


--
-- TOC entry 222 (class 1259 OID 40973)
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
    product_id integer NOT NULL,
    category_id integer NOT NULL,
    product_name character varying(150) NOT NULL,
    brand character varying(100) NOT NULL,
    description text,
    price numeric(10,2) NOT NULL,
    stock_quantity integer DEFAULT 0 NOT NULL,
    image_url text
);


ALTER TABLE public.products OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 40972)
-- Name: products_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.products_product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.products_product_id_seq OWNER TO postgres;

--
-- TOC entry 5168 (class 0 OID 0)
-- Dependencies: 221
-- Name: products_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.products_product_id_seq OWNED BY public.products.product_id;


--
-- TOC entry 4936 (class 2604 OID 41140)
-- Name: Products ProductsID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Products" ALTER COLUMN "ProductsID" SET DEFAULT nextval('public."Products_ProductsID_seq"'::regclass);


--
-- TOC entry 4934 (class 2604 OID 41126)
-- Name: User UserID; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User" ALTER COLUMN "UserID" SET DEFAULT nextval('public."User_UserID_seq"'::regclass);


--
-- TOC entry 4943 (class 2604 OID 49353)
-- Name: api_mail_outbox mail_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_mail_outbox ALTER COLUMN mail_id SET DEFAULT nextval('public.api_mail_outbox_mail_id_seq'::regclass);


--
-- TOC entry 4928 (class 2604 OID 41039)
-- Name: appointments appointment_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments ALTER COLUMN appointment_id SET DEFAULT nextval('public.appointments_appointment_id_seq'::regclass);


--
-- TOC entry 4922 (class 2604 OID 40965)
-- Name: categories category_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories ALTER COLUMN category_id SET DEFAULT nextval('public.categories_category_id_seq'::regclass);


--
-- TOC entry 4925 (class 2604 OID 40997)
-- Name: customers customer_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers ALTER COLUMN customer_id SET DEFAULT nextval('public.customers_customer_id_seq'::regclass);


--
-- TOC entry 4926 (class 2604 OID 41013)
-- Name: devices device_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.devices ALTER COLUMN device_id SET DEFAULT nextval('public.devices_device_id_seq'::regclass);


--
-- TOC entry 4927 (class 2604 OID 41023)
-- Name: employees employee_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees ALTER COLUMN employee_id SET DEFAULT nextval('public.employees_employee_id_seq'::regclass);


--
-- TOC entry 4930 (class 2604 OID 41071)
-- Name: inquiries inquiry_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inquiries ALTER COLUMN inquiry_id SET DEFAULT nextval('public.inquiries_inquiry_id_seq'::regclass);


--
-- TOC entry 4932 (class 2604 OID 41107)
-- Name: password_reset_tokens reset_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens ALTER COLUMN reset_id SET DEFAULT nextval('public.password_reset_tokens_reset_id_seq'::regclass);


--
-- TOC entry 4923 (class 2604 OID 40976)
-- Name: products product_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products ALTER COLUMN product_id SET DEFAULT nextval('public.products_product_id_seq'::regclass);


--
-- TOC entry 4980 (class 2606 OID 41146)
-- Name: Products Products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Products"
    ADD CONSTRAINT "Products_pkey" PRIMARY KEY ("ProductsID");


--
-- TOC entry 4976 (class 2606 OID 41135)
-- Name: User User_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User"
    ADD CONSTRAINT "User_email_key" UNIQUE (email);


--
-- TOC entry 4978 (class 2606 OID 41133)
-- Name: User User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User"
    ADD CONSTRAINT "User_pkey" PRIMARY KEY ("UserID");


--
-- TOC entry 4983 (class 2606 OID 49304)
-- Name: api_auth_tokens api_auth_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_auth_tokens
    ADD CONSTRAINT api_auth_tokens_pkey PRIMARY KEY (token_hash);


--
-- TOC entry 4993 (class 2606 OID 49367)
-- Name: api_mail_outbox api_mail_outbox_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_mail_outbox
    ADD CONSTRAINT api_mail_outbox_pkey PRIMARY KEY (mail_id);


--
-- TOC entry 4986 (class 2606 OID 49330)
-- Name: api_password_resets api_password_resets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_password_resets
    ADD CONSTRAINT api_password_resets_pkey PRIMARY KEY (request_id);


--
-- TOC entry 4988 (class 2606 OID 49332)
-- Name: api_password_resets api_password_resets_reset_token_hash_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_password_resets
    ADD CONSTRAINT api_password_resets_reset_token_hash_key UNIQUE (reset_token_hash);


--
-- TOC entry 4990 (class 2606 OID 49348)
-- Name: api_reset_limits api_reset_limits_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_reset_limits
    ADD CONSTRAINT api_reset_limits_pkey PRIMARY KEY (limit_key);


--
-- TOC entry 4967 (class 2606 OID 41051)
-- Name: appointments appointments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_pkey PRIMARY KEY (appointment_id);


--
-- TOC entry 4953 (class 2606 OID 40971)
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (category_id);


--
-- TOC entry 4957 (class 2606 OID 41008)
-- Name: customers customers_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_email_key UNIQUE (email);


--
-- TOC entry 4959 (class 2606 OID 41006)
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (customer_id);


--
-- TOC entry 4961 (class 2606 OID 41018)
-- Name: devices devices_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.devices
    ADD CONSTRAINT devices_pkey PRIMARY KEY (device_id);


--
-- TOC entry 4963 (class 2606 OID 41032)
-- Name: employees employees_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (employee_id);


--
-- TOC entry 4965 (class 2606 OID 41034)
-- Name: employees employees_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_username_key UNIQUE (username);


--
-- TOC entry 4969 (class 2606 OID 41080)
-- Name: inquiries inquiries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inquiries
    ADD CONSTRAINT inquiries_pkey PRIMARY KEY (inquiry_id);


--
-- TOC entry 4972 (class 2606 OID 41092)
-- Name: inquiry_products inquiry_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inquiry_products
    ADD CONSTRAINT inquiry_products_pkey PRIMARY KEY (inquiry_id, product_id);


--
-- TOC entry 4974 (class 2606 OID 41116)
-- Name: password_reset_tokens password_reset_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (reset_id);


--
-- TOC entry 4955 (class 2606 OID 40987)
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (product_id);


--
-- TOC entry 4981 (class 1259 OID 49315)
-- Name: api_auth_tokens_expiry_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX api_auth_tokens_expiry_idx ON public.api_auth_tokens USING btree (expires_at);


--
-- TOC entry 4991 (class 1259 OID 49373)
-- Name: api_mail_outbox_pending_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX api_mail_outbox_pending_idx ON public.api_mail_outbox USING btree (status, available_at);


--
-- TOC entry 4984 (class 1259 OID 49338)
-- Name: api_password_resets_customer_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX api_password_resets_customer_idx ON public.api_password_resets USING btree (customer_id);


--
-- TOC entry 4970 (class 1259 OID 49375)
-- Name: inquiries_unread_admin_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX inquiries_unread_admin_idx ON public.inquiries USING btree (inquiry_id) WHERE (admin_read_at IS NULL);


--
-- TOC entry 5002 (class 2606 OID 49305)
-- Name: api_auth_tokens api_auth_tokens_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_auth_tokens
    ADD CONSTRAINT api_auth_tokens_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(customer_id) ON DELETE CASCADE;


--
-- TOC entry 5003 (class 2606 OID 49310)
-- Name: api_auth_tokens api_auth_tokens_employee_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_auth_tokens
    ADD CONSTRAINT api_auth_tokens_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES public.employees(employee_id) ON DELETE CASCADE;


--
-- TOC entry 5005 (class 2606 OID 49368)
-- Name: api_mail_outbox api_mail_outbox_request_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_mail_outbox
    ADD CONSTRAINT api_mail_outbox_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.api_password_resets(request_id) ON DELETE CASCADE;


--
-- TOC entry 5004 (class 2606 OID 49333)
-- Name: api_password_resets api_password_resets_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.api_password_resets
    ADD CONSTRAINT api_password_resets_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(customer_id) ON DELETE CASCADE;


--
-- TOC entry 4995 (class 2606 OID 41052)
-- Name: appointments fk_appointments_customer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fk_appointments_customer FOREIGN KEY (customer_id) REFERENCES public.customers(customer_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 4996 (class 2606 OID 41062)
-- Name: appointments fk_appointments_device; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fk_appointments_device FOREIGN KEY (device_id) REFERENCES public.devices(device_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4997 (class 2606 OID 41057)
-- Name: appointments fk_appointments_employee; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fk_appointments_employee FOREIGN KEY (employee_id) REFERENCES public.employees(employee_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 4998 (class 2606 OID 41081)
-- Name: inquiries fk_inquiries_customer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inquiries
    ADD CONSTRAINT fk_inquiries_customer FOREIGN KEY (customer_id) REFERENCES public.customers(customer_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 4999 (class 2606 OID 41093)
-- Name: inquiry_products fk_inquiry_products_inquiry; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inquiry_products
    ADD CONSTRAINT fk_inquiry_products_inquiry FOREIGN KEY (inquiry_id) REFERENCES public.inquiries(inquiry_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 5000 (class 2606 OID 41098)
-- Name: inquiry_products fk_inquiry_products_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inquiry_products
    ADD CONSTRAINT fk_inquiry_products_product FOREIGN KEY (product_id) REFERENCES public.products(product_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 5001 (class 2606 OID 41117)
-- Name: password_reset_tokens fk_password_reset_customer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT fk_password_reset_customer FOREIGN KEY (customer_id) REFERENCES public.customers(customer_id) ON DELETE CASCADE;


--
-- TOC entry 4994 (class 2606 OID 40988)
-- Name: products fk_products_category; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES public.categories(category_id) ON UPDATE CASCADE ON DELETE RESTRICT;


-- Completed on 2026-10-15 12:29:00

--
-- PostgreSQL database dump complete
--

\unrestrict YiStJfqSbiDH9X8ZPAJhr0PGpE3iP8seq2os9rOZSx6wVPV4kpw5dNnOpOq9Ah3

