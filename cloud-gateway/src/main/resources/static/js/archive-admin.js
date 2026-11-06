/**
 * 档案管理（真实 API，文件存储在网关 data/archives 目录）。
 */
const ArchiveAdmin = (function () {
    const API = '/api/archives';

    function authHeaders() {
        const headers = { 'X-Trace-Id': 'trace-' + Date.now() };
        const token = AppSession.getToken();
        if (!token) {
            AppSession.clear();
            window.location.replace('/login.html');
            throw new Error('未登录或登录已过期，请重新登录');
        }
        headers['Authorization'] = 'Bearer ' + token;
        return headers;
    }

    async function apiJson(method, path, body) {
        const opts = { method, headers: Object.assign({ 'Content-Type': 'application/json' }, authHeaders()) };
        if (body != null) {
            opts.body = JSON.stringify(body);
        }
        const res = await fetch(API + path, opts);
        const json = await res.json();
        if (json.code === 401 || res.status === 401) {
            AppSession.clear();
            window.location.replace('/login.html');
            throw new Error(json.message || '登录已过期，请重新登录');
        }
        if (json.code !== 200) {
            throw new Error(json.message || '请求失败');
        }
        return json.data;
    }

    async function loadList() {
        return apiJson('GET', '');
    }

    function formatSize(n) {
        if (n == null) return '-';
        if (n < 1024) return n + ' B';
        if (n < 1024 * 1024) return (n / 1024).toFixed(1) + ' KB';
        return (n / 1024 / 1024).toFixed(2) + ' MB';
    }

    function escapeHtml(s) {
        return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    }

    return {
        mount: function (root) {
            root.innerHTML =
                '<div class="crud-toolbar">' +
                '<div class="crud-toolbar-left"><h2>档案管理</h2>' +
                '<p class="hint" id="archiveStorageHint">文件保存在网关应用目录 data/archives</p></div>' +
                '<div class="crud-toolbar-right">' +
                '<button type="button" class="btn btn-sm" id="btnArchiveAdd">新增档案</button>' +
                '<button type="button" class="btn btn-ghost btn-sm" id="btnArchiveRefresh">刷新</button>' +
                '</div></div>' +
                '<div class="table-wrap card"><table class="data-table"><thead><tr>' +
                '<th>ID</th><th>标题</th><th>分类</th><th>类型</th><th>文件名</th><th>大小</th><th>更新时间</th><th style="width:220px">操作</th>' +
                '</tr></thead><tbody id="archiveTableBody"></tbody></table></div>' +
                '<div class="card" id="archivePreviewCard" style="display:none">' +
                '<h3>图片预览</h3><div id="archivePreviewBox"></div></div>' +
                '<div class="modal-overlay" id="archiveModal" hidden>' +
                '<div class="modal" style="max-width:520px">' +
                '<div class="modal-header"><h3 class="modal-title" id="archiveModalTitle">档案</h3>' +
                '<button type="button" class="modal-close" id="archiveModalClose">&times;</button></div>' +
                '<form id="archiveForm" class="modal-form">' +
                '<label>标题 *</label><input name="title"/>' +
                '<label>分类</label><input name="category" placeholder="如：合同/证照"/>' +
                '<label>描述</label><input name="description"/>' +
                '<label id="archiveUploadLabel" style="display:none">上传文件 / 图片</label>' +
                '<input type="file" id="archiveFileInput" style="display:none" accept="image/*,.pdf,.doc,.docx,.zip"/>' +
                '</form>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-ghost" id="archiveModalCancel">取消</button>' +
                '<button type="submit" class="btn" form="archiveForm">保存</button>' +
                '</div></div></div>';

            const tbody = root.querySelector('#archiveTableBody');
            const modal = root.querySelector('#archiveModal');
            const form = root.querySelector('#archiveForm');
            const fileInput = root.querySelector('#archiveFileInput');
            const uploadLabel = root.querySelector('#archiveUploadLabel');
            let editingId = null;
            let pendingUpload = false;

            async function refresh() {
                try {
                    const path = await apiJson('GET', '/storage-path');
                    root.querySelector('#archiveStorageHint').textContent =
                        '文件存储路径：' + path;
                } catch (e) { /* ignore */ }
                const list = await loadList();
                if (!list.length) {
                    tbody.innerHTML = '<tr><td colspan="8" class="empty-cell">暂无档案，请点击「新增档案」</td></tr>';
                    return;
                }
                let html = '';
                list.forEach(function (r) {
                    html += '<tr>' +
                        '<td>' + r.id + '</td>' +
                        '<td>' + escapeHtml(r.title) + '</td>' +
                        '<td>' + escapeHtml(r.category || '-') + '</td>' +
                        '<td>' + escapeHtml(r.fileType || '-') + '</td>' +
                        '<td>' + escapeHtml(r.originalFileName || '-') + '</td>' +
                        '<td>' + formatSize(r.fileSize) + '</td>' +
                        '<td>' + escapeHtml(r.updateTime || '-') + '</td>' +
                        '<td class="actions">' +
                        '<button type="button" class="btn-link" data-edit="' + r.id + '">编辑</button>' +
                        '<button type="button" class="btn-link" data-upload="' + r.id + '">上传</button>';
                    if (r.storedFileName) {
                        html += '<button type="button" class="btn-link" data-dl="' + r.id + '">下载</button>';
                        if (r.fileType === 'IMAGE') {
                            html += '<button type="button" class="btn-link" data-preview="' + r.id + '">预览</button>';
                        }
                    }
                    html += '<button type="button" class="btn-link danger" data-del="' + r.id + '">删除</button>' +
                        '</td></tr>';
                });
                tbody.innerHTML = html;
                bindRowActions();
            }

            function bindRowActions() {
                tbody.querySelectorAll('[data-edit]').forEach(function (btn) {
                    btn.onclick = function () { openEdit(Number(btn.getAttribute('data-edit'))); };
                });
                tbody.querySelectorAll('[data-upload]').forEach(function (btn) {
                    btn.onclick = function () { openUpload(Number(btn.getAttribute('data-upload'))); };
                });
                tbody.querySelectorAll('[data-dl]').forEach(function (btn) {
                    btn.onclick = function () { downloadFile(Number(btn.getAttribute('data-dl'))); };
                });
                tbody.querySelectorAll('[data-preview]').forEach(function (btn) {
                    btn.onclick = function () { previewImage(Number(btn.getAttribute('data-preview'))); };
                });
                tbody.querySelectorAll('[data-del]').forEach(function (btn) {
                    btn.onclick = async function () {
                        if (!confirm('确定删除该档案及文件？')) return;
                        await apiJson('DELETE', '/' + btn.getAttribute('data-del'));
                        refresh();
                    };
                });
            }

            function openModal(title, showUpload) {
                root.querySelector('#archiveModalTitle').textContent = title;
                uploadLabel.style.display = showUpload ? 'block' : 'none';
                fileInput.style.display = showUpload ? 'block' : 'none';
                modal.hidden = false;
            }

            function closeModal() {
                modal.hidden = true;
                editingId = null;
                pendingUpload = false;
                form.reset();
                fileInput.value = '';
            }

            function openCreate() {
                editingId = null;
                pendingUpload = false;
                form.reset();
                openModal('新增档案', true);
            }

            async function openEdit(id) {
                const r = await apiJson('GET', '/' + id);
                editingId = id;
                pendingUpload = false;
                form.title.value = r.title || '';
                form.category.value = r.category || '';
                form.description.value = r.description || '';
                openModal('编辑档案', false);
            }

            function openUpload(id) {
                editingId = id;
                pendingUpload = true;
                form.reset();
                openModal('上传文件', true);
            }

            async function downloadFile(id) {
                const res = await fetch(API + '/' + id + '/file', { headers: authHeaders() });
                if (!res.ok) {
                    throw new Error('下载失败 HTTP ' + res.status);
                }
                const blob = await res.blob();
                const disp = res.headers.get('Content-Disposition') || '';
                let name = 'download';
                const m = /filename="?([^";]+)"?/.exec(disp);
                if (m) name = decodeURIComponent(m[1]);
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = name;
                a.click();
                URL.revokeObjectURL(url);
            }

            async function previewImage(id) {
                const res = await fetch(API + '/' + id + '/file', { headers: authHeaders() });
                if (!res.ok) throw new Error('预览失败');
                const blob = await res.blob();
                const url = URL.createObjectURL(blob);
                const card = root.querySelector('#archivePreviewCard');
                const box = root.querySelector('#archivePreviewBox');
                card.style.display = 'block';
                box.innerHTML = '<img src="' + url + '" alt="preview" style="max-width:100%;max-height:360px;border-radius:8px"/>';
            }

            form.onsubmit = async function (e) {
                e.preventDefault();
                const body = {
                    title: form.title.value.trim(),
                    category: form.category.value.trim(),
                    description: form.description.value.trim()
                };
                try {
                    let record;
                    if (pendingUpload && editingId) {
                        if (!fileInput.files || !fileInput.files.length) {
                            alert('请选择文件');
                            return;
                        }
                        record = await uploadMultipart(editingId, fileInput.files[0]);
                    } else if (editingId) {
                        if (!body.title) {
                            alert('标题不能为空');
                            return;
                        }
                        record = await apiJson('PUT', '/' + editingId, body);
                    } else {
                        if (!body.title) {
                            alert('标题不能为空');
                            return;
                        }
                        record = await apiJson('POST', '', body);
                        if (fileInput.files && fileInput.files.length) {
                            record = await uploadMultipart(record.id, fileInput.files[0]);
                        }
                    }
                    closeModal();
                    refresh();
                    if (record && record.fileType === 'IMAGE' && record.storedFileName) {
                        previewImage(record.id);
                    }
                } catch (err) {
                    alert(err.message || '操作失败');
                }
            };

            async function uploadMultipart(id, file) {
                const fd = new FormData();
                fd.append('file', file);
                const res = await fetch(API + '/' + id + '/upload', {
                    method: 'POST',
                    headers: authHeaders(),
                    body: fd
                });
                const json = await res.json();
                if (json.code !== 200) {
                    throw new Error(json.message || '上传失败');
                }
                return json.data;
            }

            root.querySelector('#btnArchiveAdd').onclick = openCreate;
            root.querySelector('#btnArchiveRefresh').onclick = refresh;
            root.querySelector('#archiveModalClose').onclick = closeModal;
            root.querySelector('#archiveModalCancel').onclick = closeModal;
            modal.addEventListener('click', function (e) {
                if (e.target === modal) closeModal();
            });

            refresh().catch(function (e) {
                tbody.innerHTML = '<tr><td colspan="8" class="empty-cell">' + escapeHtml(e.message) + '</td></tr>';
            });
        }
    };
})();
