const currency = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
});

document.addEventListener("DOMContentLoaded", () => {
  const page = document.body.dataset.page;

  if (page === "login") {
    bindLoginForm();
  }

  if (page === "register") {
    bindRegisterForm();
  }

  if (page === "portal") {
    loadPortal();
  }
});

async function apiRequest(url, options = {}) {
  const response = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    credentials: "same-origin",
    ...options,
  });

  let payload = {};
  try {
    payload = await response.json();
  } catch (_error) {
    payload = {};
  }

  if (!response.ok) {
    throw new Error(payload.message || "The request could not be completed.");
  }

  return payload;
}

function bindLoginForm() {
  const form = document.getElementById("login-form");
  const message = document.getElementById("login-message");

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(message, "Signing you in...");

    const formData = new FormData(form);
    const body = JSON.stringify({
      email: formData.get("email"),
      password: formData.get("password"),
    });

    try {
      await apiRequest("api/auth/login", {
        method: "POST",
        body,
      });

      setMessage(message, "Success. Redirecting...", "success");
      window.location.href = "portal.html";
    } catch (error) {
      setMessage(message, error.message, "error");
    }
  });
}

function bindRegisterForm() {
  const form = document.getElementById("register-form");
  const message = document.getElementById("register-message");

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(message, "Creating your account...");

    const formData = new FormData(form);
    const body = JSON.stringify({
      fullName: formData.get("fullName"),
      email: formData.get("email"),
      password: formData.get("password"),
    });

    try {
      await apiRequest("api/auth/register", {
        method: "POST",
        body,
      });

      setMessage(message, "Account created. Redirecting...", "success");
      window.location.href = "portal.html";
    } catch (error) {
      setMessage(message, error.message, "error");
    }
  });
}

async function loadPortal() {
  const message = document.getElementById("portal-message");

  try {
    const [me, ledger] = await Promise.all([
      apiRequest("api/me"),
      apiRequest("api/transactions"),
    ]);

    renderCustomer(me.customer);
    renderSummary(ledger.summary);
    renderTransactions(ledger.transactions);
    bindLogout();
    setMessage(message, "");
  } catch (error) {
    if (error.message === "You are not signed in." || error.message === "Your session is no longer valid.") {
      window.location.href = "login.html";
      return;
    }

    setMessage(message, error.message, "error");
  }
}

function renderCustomer(customer) {
  document.getElementById("portal-name").textContent = customer.fullName;
  document.getElementById("portal-email").textContent = customer.email;
}

function renderSummary(summary) {
  document.getElementById("balance-owed").textContent = currency.format(summary.balanceOwed);
  document.getElementById("total-charges").textContent = currency.format(summary.totalCharges);
  document.getElementById("total-payments").textContent = currency.format(summary.totalPayments);
}

function renderTransactions(transactions) {
  const tbody = document.getElementById("transactions-body");

  if (!transactions.length) {
    tbody.innerHTML = "<tr><td colspan=\"4\">No transactions yet.</td></tr>";
    return;
  }

  tbody.innerHTML = transactions.map((transaction) => {
    const amountClass = transaction.amount < 0 ? "amount is-credit" : "amount is-debit";
    return `
      <tr>
        <td>${escapeHtml(transaction.postedAt)}</td>
        <td>${escapeHtml(transaction.description)}</td>
        <td><span class="pill">${escapeHtml(transaction.entryType)}</span></td>
        <td class="${amountClass}">${currency.format(transaction.amount)}</td>
      </tr>
    `;
  }).join("");
}

function bindLogout() {
  const logoutButton = document.getElementById("logout-button");
  const message = document.getElementById("portal-message");

  logoutButton.addEventListener("click", async () => {
    try {
      await apiRequest("api/auth/logout", {
        method: "POST",
        body: JSON.stringify({}),
      });

      window.location.href = "login.html";
    } catch (error) {
      setMessage(message, error.message, "error");
    }
  });
}

function setMessage(element, text, state = "") {
  element.textContent = text;
  element.classList.remove("is-error", "is-success");

  if (state === "error") {
    element.classList.add("is-error");
  }

  if (state === "success") {
    element.classList.add("is-success");
  }
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#39;");
}
