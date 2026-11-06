/**
 * 主题切换。
 */
const AppTheme = (function () {
    const STORAGE_KEY = 'sc_demo_theme';
    const DEFAULT = 'light';
    const VERSION = '2';

    const THEMES = [
        { id: 'midnight', label: '深夜蓝', swatch: ['#0b1220', '#3b82f6'] },
        { id: 'ocean', label: '海洋青', swatch: ['#071a24', '#06b6d4'] },
        { id: 'forest', label: '森林绿', swatch: ['#0a1510', '#22c55e'] },
        { id: 'sunset', label: '暮光紫', swatch: ['#1a1018', '#f472b6'] },
        { id: 'amber', label: '琥珀金', swatch: ['#14100a', '#f59e0b'] },
        { id: 'light', label: '浅色', swatch: ['#f1f5f9', '#2563eb'] }
    ];

    let panelCloseBound = false;

    function themeLabel(id) {
        const t = THEMES.find(function (x) { return x.id === id; });
        return t ? t.label : id;
    }

    function isValidTheme(id) {
        return !!id && THEMES.some(function (t) { return t.id === id; });
    }

    function get() {
        try {
            const saved = localStorage.getItem(STORAGE_KEY);
            if (isValidTheme(saved)) {
                return saved;
            }
        } catch (e) { /* ignore */ }
        return DEFAULT;
    }

    /** 刷新顶栏按钮文字与面板内选中态 */
    function refreshUi(id) {
        const label = themeLabel(id);

        document.querySelectorAll('#themeCurrentLabel, [data-theme-current-label]').forEach(function (el) {
            el.textContent = label;
        });

        document.querySelectorAll('[data-theme-option]').forEach(function (btn) {
            const active = btn.getAttribute('data-theme-option') === id;
            btn.classList.toggle('is-active', active);
            btn.setAttribute('aria-pressed', active ? 'true' : 'false');
        });
    }

    function apply(themeId) {
        const id = isValidTheme(themeId) ? themeId : DEFAULT;
        document.documentElement.setAttribute('data-theme', id);
        try {
            localStorage.setItem(STORAGE_KEY, id);
        } catch (e) { /* ignore */ }
        refreshUi(id);
        return id;
    }

    function init() {
        apply(get());
    }

    function bindGlobalEvents() {
        if (panelCloseBound) return;
        panelCloseBound = true;

        document.addEventListener('click', function (e) {
            const optionBtn = e.target.closest('[data-theme-option]');
            if (optionBtn) {
                e.preventDefault();
                e.stopPropagation();
                apply(optionBtn.getAttribute('data-theme-option'));
                const switcher = optionBtn.closest('.theme-switcher');
                const panel = switcher && switcher.querySelector('[data-theme-panel]');
                const trigger = switcher && switcher.querySelector('[data-theme-trigger]');
                if (panel) panel.hidden = true;
                if (trigger) trigger.setAttribute('aria-expanded', 'false');
                return;
            }

            const trigger = e.target.closest('[data-theme-trigger]');
            if (trigger) {
                e.stopPropagation();
                const switcher = trigger.closest('.theme-switcher');
                const panel = switcher && switcher.querySelector('[data-theme-panel]');
                if (!panel) return;
                const open = panel.hidden;
                panel.hidden = !open;
                trigger.setAttribute('aria-expanded', open ? 'true' : 'false');
                return;
            }

            document.querySelectorAll('.theme-switcher [data-theme-panel]').forEach(function (panel) {
                if (!panel.hidden) panel.hidden = true;
            });
            document.querySelectorAll('[data-theme-trigger]').forEach(function (trigger) {
                trigger.setAttribute('aria-expanded', 'false');
            });
        });
    }

    function mountPicker(root) {
        if (!root) return;

        bindGlobalEvents();

        const current = get();
        let html = '<div class="theme-switcher">';
        html += '<button type="button" class="btn btn-ghost btn-sm theme-trigger" data-theme-trigger aria-haspopup="true" aria-expanded="false">';
        html += '<span class="theme-trigger-icon" aria-hidden="true">◐</span>';
        html += '<span id="themeCurrentLabel" data-theme-current-label">' + themeLabel(current) + '</span>';
        html += '</button>';
        html += '<div class="theme-panel" data-theme-panel hidden>';
        html += '<p class="theme-panel-title">主题背景</p><div class="theme-grid">';
        THEMES.forEach(function (t) {
            const active = t.id === current;
            html += '<button type="button" class="theme-option' + (active ? ' is-active' : '') + '" data-theme-option="' + t.id + '" aria-pressed="' + (active ? 'true' : 'false') + '">';
            html += '<span class="theme-swatch" style="background:linear-gradient(135deg,' + t.swatch[0] + ' 50%,' + t.swatch[1] + ' 50%)"></span>';
            html += '<span class="theme-option-label">' + t.label + '</span></button>';
        });
        html += '</div></div></div>';
        root.innerHTML = html;

        apply(get());
    }

    return { THEMES, get, apply, init, mountPicker, refreshUi, STORAGE_KEY, DEFAULT, VERSION };
})();

(function () {
    try {
        const saved = localStorage.getItem('sc_demo_theme');
        if (saved && AppTheme.THEMES.some(function (t) { return t.id === saved; })) {
            document.documentElement.setAttribute('data-theme', saved);
        }
    } catch (e) { /* ignore */ }
})();
