$(function () {
  var path = location.pathname;
  // 仅在Admin相关页面下执行布局注入逻辑（排除登录页和非Admin页）
  if (path === '/' || /\/index\.html$/.test(path) || !/^\/admin\//.test(path)) {
    return;
  }

  // --- 创建顶部导航栏 (AppBar) ---
  var appbar = $('<div class="ui-appbar"></div>');
  var brand = $('<div class="brand"></div>');
  brand.append('<i class="bi bi-book-fill"></i>');
  brand.append('<span>图书管理系统</span>');
  var grow = $('<div class="grow"></div>'); // 占位符，撑开中间空间
  var actions = $('<div class="actions"></div>');
  actions.append('<i class="bi bi-bell"></i>'); // 通知图标
  var avatar = $('<div class="avatar"></div>'); // 用户头像
  actions.append(avatar);
  appbar.append(brand).append(grow).append(actions);

  // --- 创建侧边栏和内容区布局 ---
  var layout = $('<div class="ui-layout"></div>');
  var sidebar = $('<div class="ui-sidebar"></div>');
  var content = $('<div class="ui-content"></div>');
  layout.append(sidebar).append(content);

  // 辅助函数：创建侧边栏菜单组
  function makeGroup(title, items) {
    var g = $('<div class="group"></div>');
    var t = $('<button class="group-toggle btn btn-link w-100 text-start"></button>');
    t.text(title);
    var ul = $('<div class="items"></div>');
    items.forEach(function (it) {
      var a = $('<a class="item" href="#"></a>');
      a.attr('href', it.href);
      a.append('<i class="' + it.icon + '"></i>');
      a.append('<span>' + it.text + '</span>');
      // 高亮当前选中的菜单项
      if (path.indexOf(it.href) !== -1) a.addClass('active');
      ul.append(a);
    });
    g.append(t).append(ul);
    t.on('click', function () { g.toggleClass('open'); });
    return g;
  }

  // 定义菜单结构
  var groups = [
    { title: '控制台', items: [{ text: '主页', href: '/admin/main.html', icon: 'bi bi-house-door' }] },
    {
      title: '图书管理', items: [
        { text: '图书列表', href: '/admin/books.html', icon: 'bi bi-book' },
        { text: '新增图书', href: '/admin/add-book.html', icon: 'bi bi-plus-circle' }
      ]
    },
    {
      title: '读者管理', items: [
        { text: '读者列表', href: '/admin/readers.html', icon: 'bi bi-people' },
        { text: '新增读者', href: '/admin/add-reader.html', icon: 'bi bi-person-plus' }
      ]
    },
    {
      title: '借还管理', items: [
        { text: '借还记录', href: '/admin/lend-records.html', icon: 'bi bi-journal-text' }
      ]
    },
    {
      title: '系统设置', items: [
        { text: '设置', href: '/admin/settings.html', icon: 'bi bi-gear' }
      ]
    }
  ];

  // 渲染所有菜单组
  groups.forEach(function (g) { sidebar.append(makeGroup(g.title, g.items)); });

  // 将构建好的AppBar和Layout注入到Body
  $('body').prepend(appbar);
  $('body').append(layout);

  // --- 用户头像下拉菜单逻辑 ---
  var menu = $('<div class="ui-dropdown" style="display:none;"></div>');
  var logoutItem = $('<a href="#" class="item">退出登录</a>');
  menu.append(logoutItem);
  $('body').append(menu);

  // 计算下拉菜单位置
  function positionMenu() {
    var rect = avatar[0].getBoundingClientRect();
    var top = rect.bottom + 8;
    var menuWidth = menu.outerWidth();
    var desiredLeft = rect.right - menuWidth;
    var safeLeft = Math.max(8, Math.min(desiredLeft, window.innerWidth - menuWidth - 8));
    menu.css({ top: top + 'px', left: safeLeft + 'px' });
  }

  // 退出登录逻辑
  function doLogout() {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('rememberedUser');
    window.location.href = '/index.html';
  }

  // 绑定事件：点击头像显示/隐藏菜单
  avatar.on('click', function (e) {
    e.stopPropagation();
    positionMenu();
    menu.toggle();
  });
  $(window).on('resize', function () { if (menu.is(':visible')) positionMenu(); });
  logoutItem.on('click', function (e) {
    e.preventDefault();
    menu.hide();
    doLogout();
  });
  $(document).on('click', function (e) {
    if (menu.is(':visible')) menu.hide();
  });

  // --- 移动原有页面内容到 Layout 的 Content 区域 ---
  var candidate = $('.content-area');
  if (candidate.length === 0) {
    candidate = $('.container');
  }
  if (candidate.length === 0) {
    // 如果没有特定容器，则选取Body下除了AppBar和Layout之外的所有元素
    candidate = $('body').children().not('.ui-appbar').not('.ui-layout');
  }

  // 移除旧的导航栏和侧边栏（如果存在）
  $('.navbar').remove();
  $('.sidebar').remove();

  if (candidate.length > 0) {
    content.append(candidate);
  }
});

// 全局通用的确认对话框函数
function uiConfirm(message, options) {
  options = options || {};
  var okText = options.okText || '确定';
  var cancelText = options.cancelText || '取消';
  return new Promise(function (resolve) {
    var modal = $('#uiConfirmModal');
    // 如果模态框不存在，则动态创建
    if (modal.length === 0) {
      var html = '' +
        '<div class="modal fade" id="uiConfirmModal" tabindex="-1">' +
        '  <div class="modal-dialog">' +
        '    <div class="modal-content">' +
        '      <div class="modal-header">' +
        '        <h5 class="modal-title">提示</h5>' +
        '        <button type="button" class="close" data-dismiss="modal">&times;</button>' +
        '      </div>' +
        '      <div class="modal-body"><p id="uiConfirmMessage"></p></div>' +
        '      <div class="modal-footer">' +
        '        <button type="button" class="btn btn-primary" id="uiConfirmOk"></button>' +
        '        <button type="button" class="btn btn-secondary" data-dismiss="modal" id="uiConfirmCancel"></button>' +
        '      </div>' +
        '    </div>' +
        '  </div>' +
        '</div>';
      $('body').append(html);
      modal = $('#uiConfirmModal');
    }
    $('#uiConfirmMessage').text(message || '');
    $('#uiConfirmOk').text(okText);
    $('#uiConfirmCancel').text(cancelText);
    var done = false;
    // 绑定确定按钮
    $('#uiConfirmOk').off('click').on('click', function () {
      done = true;
      resolve(true);
      modal.modal('hide');
    });
    // 绑定取消按钮
    $('#uiConfirmCancel').off('click').on('click', function () {
      done = true;
      resolve(false);
    });
    // 模态框关闭时的处理
    modal.off('hidden.bs.modal').on('hidden.bs.modal', function () {
      if (!done) resolve(false);
    });
    modal.modal('show');
  });
}
