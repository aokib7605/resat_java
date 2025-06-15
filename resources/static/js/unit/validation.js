// 正規表現パターンの定義
const idPattern = /^[a-zA-Z0-9]{4,20}$/;
const passPattern = /^(?=.*[A-Z])[a-zA-Z0-9]{4,20}$/;

function validateCLoginForm(event) {
  const customerId = document.forms["loginForm"]["customerId"].value;
  const customerPass = document.forms["loginForm"]["customerPass"].value;

  const messageElement = document.getElementById("errorMessage");

  if (!idPattern.test(customerId)) {
    messageElement.textContent = "IDは8〜20文字の英数字で入力してください。";
    event.preventDefault();
    return false;
  }

  if (!passPattern.test(customerPass)) {
    messageElement.textContent =
      "パスワードは大文字を含んだ8〜20文字の半角英数字で入力してください。";
    event.preventDefault();
    return false;
  }

  messageElement.textContent = "";
  return true;
}

function validateNewCustomerForm(event) {
  const customerId = document.forms["newCustomerForm"]["customerId"].value;
  const customerPass = document.forms["newCustomerForm"]["customerPass"].value;
  const messageElement = document.getElementById("errorMessage");

  if (!idPattern.test(customerId)) {
    messageElement.textContent =
      "IDは8〜20文字の英数字または記号で入力してください。";
    event.preventDefault();
    return false;
  }

  if (!passPattern.test(customerPass)) {
    messageElement.textContent =
      "パスワードは大文字を含んだ8〜20文字の半角英数字で入力してください。";
    event.preventDefault();
    return false;
  }

  messageElement.textContent = "";
  return true;
}

function validateUpdateCPass(event) {
  const customerPass = document.forms["updateCPass"]["customerPass"].value;
  const messageElement = document.getElementById("errorMessage");

  if (!passPattern.test(customerPass)) {
    messageElement.textContent =
      "パスワードは大文字を含んだ8〜20文字の半角英数字で入力してください。";
    event.preventDefault();
    return false;
  }

  messageElement.textContent = "";
  return true;
}
