$(function () {
  // 页面加载时检查是否有记住的登录信息
  loadRememberedLogin();
  // 绑定表单提交事件
  $('#loginForm').on('submit', function (e) {
    e.preventDefault(); // 阻止默认表单提交
    performLogin(); // 执行登录逻辑
  });
});

// 执行登录的主要函数
function performLogin() {
  var username = $('#username').val().trim();
  var password = $('#password').val();
  var rememberMe = $('#rememberMe').is(':checked');
  // 简单的非空校验
  if (!username) { showError('请输入用户名'); $('#username').focus(); return; }
  if (!password) { showError('请输入密码'); $('#password').focus(); return; }
  
  // 按钮显示加载状态
  var btn = $('#loginBtn');
  var original = btn.text();
  btn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status"></span> 登录中...');
  
  // 发送AJAX请求到后端进行验证
  $.ajax({
    url: '/api/loginCheck',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ username: username, passwd: password }),
    success: function (response) {
      var code = response && response.stateCode ? response.stateCode.trim() : '';
      if (code === '0') {
        showError('账号或密码错误');
      } else if (code === '1') {
        // 管理员登录成功
        if (rememberMe) rememberLogin(username, password);
        showSuccess('登录成功，正在跳转...');
        setTimeout(function () { window.location.href = '/admin/main.html'; }, 800);
      } else if (code === '2') {
        // 读者登录成功
        if (rememberMe) rememberLogin(username, password);
        showSuccess('登录成功，正在跳转...');
        setTimeout(function () { window.location.href = '/reader/main.html'; }, 800);
      } else {
        showError('登录失败，请重试');
      }
    },
    error: function () {
      showError('网络错误，请稍后重试');
    },
    complete: function () {
      // 恢复按钮状态
      btn.prop('disabled', false).text(original);
    }
  });
}

// 显示错误信息
function showError(message) {
  $('#errorMessage').text(message);
  $('#errorAlert').removeClass('alert-success').addClass('alert-danger').show();
}

// 显示成功信息
function showSuccess(message) {
  $('#errorMessage').text(message);
  $('#errorAlert').removeClass('alert-danger').addClass('alert-success').show();
}

// 记住登录信息到本地存储
function rememberLogin(username, password) {
  localStorage.setItem('rememberedUser', JSON.stringify({ username: username, password: password, timestamp: Date.now() }));
}

// 从本地存储加载登录信息
function loadRememberedLogin() {
  var remembered = localStorage.getItem('rememberedUser');
  if (!remembered) return;
  try {
    var data = JSON.parse(remembered);
    // 检查是否在7天有效期内
    if (Date.now() - data.timestamp < 7 * 24 * 60 * 60 * 1000) {
      $('#username').val(data.username);
      $('#password').val(data.password);
      $('#rememberMe').prop('checked', true);
    } else {
      localStorage.removeItem('rememberedUser');
    }
  } catch (e) {
    localStorage.removeItem('rememberedUser');
  }
}
