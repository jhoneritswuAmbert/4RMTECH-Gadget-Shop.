package com.fourrmtech.app.models;
import java.util.List;
public class MarkInquiriesReadRequest {
 private final List<Integer> inquiry_ids;
 public MarkInquiriesReadRequest(List<Integer> ids) { this.inquiry_ids = ids; }
}
