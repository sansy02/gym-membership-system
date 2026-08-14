let currentUser = { name: "", role: "" };
let selectedMemberId = -1;
let membersCache = [];
let plansCache = [];

async function api(method, path, data) {
  const options = { method };
  if (data) {
    options.headers = { "Content-Type": "application/json" };
    options.body = JSON.stringify(data);
  }
  const res = await fetch(path, options);
  return res.json();
}

async function doLogin() {
  const username = document.getElementById("loginUsername").value.trim();
  const password = document.getElementById("loginPassword").value.trim();
  const errorEl = document.getElementById("loginError");

  if (!username || !password) {
    errorEl.textContent = "Please enter username and password.";
    return;
  }

  const result = await api("POST", "/api/login", { username, password });
  if (result.success) {
    currentUser = { name: result.name, role: result.role };
    document.getElementById("loggedInUser").textContent =
        result.name + " (" + result.role + ")";
    document.getElementById("loginView").style.display = "none";
    document.getElementById("appView").style.display = "flex";
    errorEl.textContent = "";
    document.getElementById("loginPassword").value = "";
    await initApp();
  } else {
    errorEl.textContent = result.message || "Login failed.";
  }
}

function doLogout() {
  currentUser = { name: "", role: "" };
  document.getElementById("appView").style.display = "none";
  document.getElementById("loginView").style.display = "flex";
  document.getElementById("loginUsername").value = "";
  document.getElementById("loginPassword").value = "";
}

document.addEventListener("keydown", (e) => {
  if (e.key === "Enter" && document.getElementById("loginView").style.display !== "none") {
    doLogin();
  }
});

async function initApp() {
  await Promise.all([
    loadPlans(),
    loadMembersForSelects(),
    switchView("dashboard"),
  ]);
}

function switchView(viewName) {
  document.querySelectorAll(".view").forEach(v => v.classList.remove("active"));
  document.querySelectorAll(".nav-btn").forEach(b => b.classList.remove("active"));
  document.querySelectorAll(`.nav-btn[data-view="${viewName}"]`)
      .forEach(b => b.classList.add("active"));

  const target = document.getElementById("view-" + viewName);
  if (target) target.classList.add("active");

  if (viewName === "dashboard") loadStats();
  if (viewName === "members") loadMembersTable();
  if (viewName === "payments") loadPaymentsTable();
  if (viewName === "attendance") loadAttendanceTable();
}

async function loadStats() {
  const s = await api("GET", "/api/stats");
  document.getElementById("statMembers").textContent = s.totalMembers ?? "0";
  document.getElementById("statPayments").textContent = "$" + (s.totalPayments ?? "0");
  document.getElementById("statCheckins").textContent = s.todayCheckins ?? "0";
}

async function loadPlans() {
  plansCache = await api("GET", "/api/plans");
  const select = document.getElementById("memberPlan");
  select.innerHTML = plansCache
      .map(p => `<option value="${p.plan_id}">${p.plan_name} ($${p.price}/mo)</option>`)
      .join("");
}

async function loadMembersForSelects() {
  membersCache = await api("GET", "/api/members");
  const optionHtml = membersCache
      .map(m => `<option value="${m.member_id}">${m.member_id} - ${m.name}</option>`)
      .join("");
  document.getElementById("paymentMember").innerHTML = optionHtml;
  document.getElementById("attendanceMember").innerHTML = optionHtml;
}

async function loadMembersTable() {
  const members = await api("GET", "/api/members");
  membersCache = members;
  const tbody = document.querySelector("#membersTable tbody");
  tbody.innerHTML = members.map(m => `
    <tr data-id="${m.member_id}" onclick="selectMemberRow(${m.member_id})">
      <td>${m.member_id}</td>
      <td>${escapeHtml(m.name)}</td>
      <td>${escapeHtml(m.email)}</td>
      <td>${escapeHtml(m.phone)}</td>
      <td>${escapeHtml(m.plan_name)}</td>
      <td>${m.join_date}</td>
    </tr>`).join("");

  const optionHtml = members
      .map(m => `<option value="${m.member_id}">${m.member_id} - ${m.name}</option>`)
      .join("");
  document.getElementById("paymentMember").innerHTML = optionHtml;
  document.getElementById("attendanceMember").innerHTML = optionHtml;
}

function selectMemberRow(id) {
  selectedMemberId = id;
  document.querySelectorAll("#membersTable tbody tr").forEach(tr => {
    tr.classList.toggle("selected", Number(tr.dataset.id) === id);
  });
  const m = membersCache.find(x => x.member_id === id);
  if (m) {
    document.getElementById("memberName").value = m.name;
    document.getElementById("memberEmail").value = m.email;
    document.getElementById("memberPhone").value = m.phone;
    document.getElementById("memberPlan").value = m.plan_id;
    document.getElementById("memberFormTitle").textContent = `Edit Member #${id}`;
    setFormMsg("memberFormMsg", "Editing member #" + id + ". Click Update to save.", "ok");
  }
}

async function addMember() {
  const data = readMemberForm();
  if (!data) return;

  const r = await api("POST", "/api/members", data);
  if (r.success) {
    setFormMsg("memberFormMsg", r.message, "ok");
    clearMemberForm();
    await loadMembersTable();
  } else {
    setFormMsg("memberFormMsg", r.message, "err");
  }
}

async function updateMember() {
  if (selectedMemberId <= 0) {
    setFormMsg("memberFormMsg", "Please select a member from the table first.", "err");
    return;
  }
  const data = readMemberForm();
  if (!data) return;
  data.member_id = selectedMemberId;

  const r = await api("PUT", "/api/members", data);
  if (r.success) {
    setFormMsg("memberFormMsg", r.message, "ok");
    clearMemberForm();
    await loadMembersTable();
  } else {
    setFormMsg("memberFormMsg", r.message, "err");
  }
}

async function deleteMember() {
  if (selectedMemberId <= 0) {
    setFormMsg("memberFormMsg", "Please select a member from the table first.", "err");
    return;
  }
  if (!confirm("Delete member #" + selectedMemberId +
               "?\nThis also deletes their payments and check-in records.")) {
    return;
  }

  const r = await api("DELETE", "/api/members?member_id=" + selectedMemberId);
  if (r.success) {
    setFormMsg("memberFormMsg", r.message, "ok");
    clearMemberForm();
    await loadMembersTable();
  } else {
    setFormMsg("memberFormMsg", r.message, "err");
  }
}

function readMemberForm() {
  const name = document.getElementById("memberName").value.trim();
  const email = document.getElementById("memberEmail").value.trim();
  const phone = document.getElementById("memberPhone").value.trim();
  const planId = Number(document.getElementById("memberPlan").value);

  if (!name || !email || !phone) {
    setFormMsg("memberFormMsg", "Please fill in name, email and phone.", "err");
    return null;
  }
  return { name, email, phone, plan_id: planId };
}

function clearMemberForm() {
  selectedMemberId = -1;
  document.getElementById("memberName").value = "";
  document.getElementById("memberEmail").value = "";
  document.getElementById("memberPhone").value = "";
  if (plansCache.length > 0) {
    document.getElementById("memberPlan").value = plansCache[0].plan_id;
  }
  document.getElementById("memberFormTitle").textContent = "Add New Member";
  setFormMsg("memberFormMsg", "", "ok");
  document.querySelectorAll("#membersTable tbody tr").forEach(tr =>
      tr.classList.remove("selected"));
}

async function addPayment() {
  const memberId = Number(document.getElementById("paymentMember").value);
  const amount = parseFloat(document.getElementById("paymentAmount").value);
  const method = document.getElementById("paymentMethod").value;

  if (!memberId || memberId <= 0) {
    setFormMsg("paymentFormMsg", "Please select a member.", "err");
    return;
  }
  if (!amount || amount <= 0) {
    setFormMsg("paymentFormMsg", "Please enter a valid amount.", "err");
    return;
  }

  const r = await api("POST", "/api/payments", {
    member_id: memberId, amount, payment_method: method
  });
  if (r.success) {
    setFormMsg("paymentFormMsg", r.message, "ok");
    document.getElementById("paymentAmount").value = "";
    await loadPaymentsTable();
    await loadStats();
  } else {
    setFormMsg("paymentFormMsg", r.message, "err");
  }
}

async function loadPaymentsTable() {
  const payments = await api("GET", "/api/payments");
  const tbody = document.querySelector("#paymentsTable tbody");
  tbody.innerHTML = payments.map(p => `
    <tr>
      <td>${p.payment_id}</td>
      <td>${escapeHtml(p.member_name)}</td>
      <td>$${Number(p.amount).toFixed(2)}</td>
      <td>${p.payment_date}</td>
      <td>${escapeHtml(p.payment_method)}</td>
    </tr>`).join("");
}

async function doCheckIn() {
  const memberId = Number(document.getElementById("attendanceMember").value);

  if (!memberId || memberId <= 0) {
    setFormMsg("attendanceFormMsg", "Please select a member.", "err");
    return;
  }

  const r = await api("POST", "/api/attendance", { member_id: memberId });
  if (r.success) {
    setFormMsg("attendanceFormMsg",
        r.message + " (Time: " + new Date().toLocaleString() + ")", "ok");
    await loadAttendanceTable();
    await loadStats();
  } else {
    setFormMsg("attendanceFormMsg", r.message, "err");
  }
}

async function loadAttendanceTable() {
  const records = await api("GET", "/api/attendance");
  const tbody = document.querySelector("#attendanceTable tbody");
  tbody.innerHTML = records.map(a => `
    <tr>
      <td>${a.attendance_id}</td>
      <td>${escapeHtml(a.member_name)}</td>
      <td>${a.check_in_time}</td>
    </tr>`).join("");
}

function setFormMsg(id, text, type) {
  const el = document.getElementById(id);
  el.textContent = text;
  el.className = "form-msg " + (type || "");
}

function escapeHtml(str) {
  if (str === null || str === undefined) return "";
  return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
}
