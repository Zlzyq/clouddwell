/**
 * 公共脚本：会话、API 请求。
 *
 * 登录态存 localStorage（多标签共享），配合心跳机制：
 * 只要还有标签页打开，心跳持续更新；所有标签都关掉后心跳停止，
 * 再打开时检测到心跳过期 → 清除登录态 → 需要重新登录。
 */
const AppSession = {
    TOKEN_KEY: 'admin_token',
    USER_KEY: 'admin_user',
    HEARTBEAT_KEY: 'admin_heartbeat',
    SESSION_TIMEOUT: 1000, // 1 秒无心跳 → 会话过期

    getToken() {
        return localStorage.getItem(this.TOKEN_KEY) || '';
    },

    getUser() {
        try {
            return JSON.parse(localStorage.getItem(this.USER_KEY) || 'null');
        } catch (e) {
            return null;
        }
    },

    save(token, user) {
        localStorage.setItem(this.TOKEN_KEY, token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    },

    clear() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        localStorage.removeItem(this.HEARTBEAT_KEY);
    },

    /** 检查是否已登录。心跳过期会自动清登录态。 */
    isLoggedIn() {
        if (!this.getToken()) return false;
        // 心跳过期 → 所有标签都关了 → 清登录态
        const lastBeat = parseInt(localStorage.getItem(this.HEARTBEAT_KEY) || '0');
        if (Date.now() - lastBeat > this.SESSION_TIMEOUT) {
            this.clear();
            return false;
        }
        return true;
    },

    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.replace('/login.html');
            return false;
        }
        return true;
    },

    redirectIfLoggedIn(target) {
        if (this.isLoggedIn()) {
            window.location.replace(target || '/admin.html');
        }
    }
};

// 开启心跳：先检测心跳是否已过期（所有标签关闭后心跳停止），
// 如果已过期 → 清登录态，不续心跳；未过期 → 启动心跳续期。
(function startHeartbeat() {
    // 先做过期检查：心跳存在但已过期 → 会话已结束，清登录态
    const lastBeat = parseInt(localStorage.getItem(AppSession.HEARTBEAT_KEY) || '0');
    if (lastBeat > 0 && Date.now() - lastBeat > AppSession.SESSION_TIMEOUT) {
        AppSession.clear();
        return;
    }
    if (!localStorage.getItem(AppSession.TOKEN_KEY)) return;

    localStorage.setItem(AppSession.HEARTBEAT_KEY, Date.now());
    setInterval(() => {
        localStorage.setItem(AppSession.HEARTBEAT_KEY, Date.now());
    }, 300);
})();

async function apiRequest(method, path, body, needAuth) {
    const headers = {
        'Content-Type': 'application/json',
        'X-Trace-Id': 'trace-' + Date.now()
    };
    if (needAuth) {
        const token = AppSession.getToken();
        if (!token) {
            AppSession.clear();
            window.location.replace('/login.html');
            throw new Error('未登录或登录已过期，请重新登录');
        }
        headers['Authorization'] = 'Bearer ' + token;
    }
    const opts = { method, headers };
    if (body != null) {
        opts.body = JSON.stringify(body);
    }
    const res = await fetch(path, opts);
    const text = await res.text();
    let json;
    try {
        json = JSON.parse(text);
    } catch (e) {
        throw new Error('HTTP ' + res.status + ': ' + text);
    }
    if (!res.ok && json.code == null) {
        json = { code: res.status, message: text, data: null };
    }
    if (needAuth && (json.code === 401 || res.status === 401)) {
        AppSession.clear();
        window.location.replace('/login.html');
        throw new Error(json.message || '登录已过期，请重新登录');
    }
    return json;
}

async function login(userId, password) {
    const res = await apiRequest('POST', '/cloud-auth/api/auth/login', { userId, password }, false);
    if (res.code !== 200 || !res.data || !res.data.accessToken) {
        throw new Error(res.message || '登录失败');
    }
    const d = res.data;
    const user = {
        userId: d.userId,
        userName: d.userName,
        role: d.role,
        avatarUrl: d.avatarUrl,
        expireInSeconds: d.expireInSeconds
    };
    AppSession.save(d.accessToken, user);
    // 登录成功后启动心跳
    localStorage.setItem(AppSession.HEARTBEAT_KEY, Date.now());
    return user;
}

function logout() {
    AppSession.clear();
    window.location.replace('/login.html');
}

function renderJson(el, data, isError) {
    if (!el) return;
    el.textContent = typeof data === 'string' ? data : JSON.stringify(data, null, 2);
    el.style.borderColor = isError ? 'var(--err)' : 'var(--border)';
}

function showAlert(el, msg, isError) {
    if (!el) return;
    el.textContent = msg;
    el.className = 'alert show' + (isError ? ' err' : '');
}
