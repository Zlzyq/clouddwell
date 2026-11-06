/**
 * 管理中心 CRUD：数据持久化在各模块 data 目录。
 */
const MockCrudRegistry = (function () {
    const API_STORAGE_PATH = '/cloud-business/api/business/storage-path';

    const STATUS_OPTIONS = [
        { value: '1', label: '启用' },
        { value: '0', label: '停用' }
    ];

    const ORDER_STATUS = [
        { value: 'PENDING', label: '待支付' },
        { value: 'PAID', label: '已支付' },
        { value: 'SHIPPED', label: '已发货' },
        { value: 'DONE', label: '已完成' },
        { value: 'CANCEL', label: '已取消' }
    ];

    function labelOf(options, value) {
        const o = options.find((x) => x.value === String(value));
        return o ? o.label : value;
    }

    function statusBadge(value, kind) {
        const label = kind === 'order' ? labelOf(ORDER_STATUS, value) : labelOf(STATUS_OPTIONS, value);
        let cls = 'badge-muted';
        if (kind === 'order') {
            if (value === 'PAID' || value === 'DONE') cls = 'badge-ok';
            else if (value === 'PENDING') cls = 'badge-warn';
            else if (value === 'SHIPPED') cls = 'badge-info';
            else if (value === 'CANCEL') cls = 'badge-danger';
        } else if (value === '1') cls = 'badge-ok';
        return '<span class="badge-pill ' + cls + '">' + escapeHtml(String(label)) + '</span>';
    }

    let toastHost = null;
    function toast(msg, ok) {
        if (!toastHost) {
            toastHost = document.createElement('div');
            toastHost.className = 'toast-host';
            document.body.appendChild(toastHost);
        }
        const el = document.createElement('div');
        el.className = 'toast toast-' + (ok ? 'ok' : 'err');
        el.innerHTML = '<span class="toast-icon">' + (ok ? '✓' : '✕') + '</span><span>' + escapeHtml(msg) + '</span>';
        toastHost.appendChild(el);
        setTimeout(function () { el.remove(); }, 2600);
    }

    const CONFIGS = {
        company: {
            title: '公司管理',
            apiBase: '/cloud-business/api/companies',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'code', label: '公司编码' },
                { key: 'name', label: '公司名称' },
                { key: 'contact', label: '联系人' },
                { key: 'phone', label: '电话' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') },
                { key: 'createTime', label: '创建时间' }
            ],
            fields: [
                { key: 'code', label: '公司编码', required: true },
                { key: 'name', label: '公司名称', required: true },
                { key: 'contact', label: '联系人' },
                { key: 'phone', label: '电话' },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ]
        },
        department: {
            title: '部门管理',
            apiBase: '/cloud-business/api/departments',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'companyName', label: '所属公司' },
                { key: 'name', label: '部门名称' },
                { key: 'leader', label: '负责人' },
                { key: 'phone', label: '电话' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') }
            ],
            fields: [
                { key: 'companyName', label: '所属公司', required: true },
                { key: 'name', label: '部门名称', required: true },
                { key: 'leader', label: '负责人' },
                { key: 'phone', label: '电话' },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ]
        },
        userMgmt: {
            title: '用户管理',
            apiBase: '/cloud-business/api/admin/users',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'loginName', label: '登录名' },
                { key: 'userName', label: '姓名' },
                { key: 'deptName', label: '部门' },
                { key: 'role', label: '角色' },
                { key: 'phone', label: '手机' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') }
            ],
            fields: [
                { key: 'loginName', label: '登录名', required: true },
                { key: 'userName', label: '姓名', required: true },
                { key: 'deptName', label: '部门' },
                { key: 'role', label: '角色', type: 'select', options: [
                    { value: '超级管理员', label: '超级管理员' },
                    { value: '运营专员', label: '运营专员' },
                    { value: '普通用户', label: '普通用户' }
                ], default: '普通用户' },
                { key: 'phone', label: '手机' },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ]
        },
        productMgmt: {
            title: '产品管理',
            apiBase: '/cloud-business/api/products',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'code', label: 'SKU 编码' },
                { key: 'name', label: '产品名称' },
                { key: 'category', label: '分类' },
                { key: 'price', label: '单价(元)' },
                { key: 'stock', label: '库存' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') }
            ],
            fields: [
                { key: 'code', label: 'SKU 编码', required: true },
                { key: 'name', label: '产品名称', required: true },
                { key: 'category', label: '分类' },
                { key: 'price', label: '单价', type: 'number', required: true },
                { key: 'stock', label: '库存', type: 'number', default: 100 },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ]
        },
        orderMgmt: {
            title: '订单管理',
            apiBase: '/cloud-business/api/admin/orders',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'orderNo', label: '订单号' },
                { key: 'buyerName', label: '买家' },
                { key: 'productName', label: '商品' },
                { key: 'quantity', label: '数量' },
                { key: 'amount', label: '金额(元)' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'order') },
                { key: 'createTime', label: '下单时间' }
            ],
            fields: [
                { key: 'orderNo', label: '订单号' },
                { key: 'buyerName', label: '买家', type: 'select', options: [
                    { value: '演示用户一号', label: '演示用户一号' },
                    { value: '演示用户二号', label: '演示用户二号' },
                    { value: '演示用户10086', label: '演示用户10086' }
                ], required: true },
                { key: 'productCode', label: 'SKU', required: true },
                { key: 'productName', label: '商品名称', required: true },
                { key: 'quantity', label: '数量', type: 'number', default: 1 },
                { key: 'amount', label: '金额', type: 'number', required: true },
                { key: 'status', label: '状态', type: 'select', options: ORDER_STATUS, default: 'PAID' },
                { key: 'createTime', label: '下单时间' }
            ],
            batchDelete: true,
            pagination: true
        },
        hotel: {
            title: '酒店管理',
            apiBase: '/cloud-hotel/api/hotels',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'name', label: '酒店名称' },
                { key: 'address', label: '地址' },
                { key: 'phone', label: '电话' },
                { key: 'starRating', label: '星级' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') },
                { key: 'createTime', label: '创建时间' }
            ],
            fields: [
                { key: 'name', label: '酒店名称', required: true },
                { key: 'address', label: '地址' },
                { key: 'phone', label: '电话' },
                { key: 'starRating', label: '星级', type: 'number', default: 3 },
                { key: 'description', label: '描述' },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ]
        },
        room: {
            title: '房间管理',
            apiBase: '/cloud-room/api/rooms',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'roomNumber', label: '房间号' },
                { key: 'type', label: '房型' },
                { key: 'price', label: '价格(元)' },
                { key: 'capacity', label: '可住人数' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') },
                { key: 'createTime', label: '创建时间' }
            ],
            fields: [
                { key: 'roomNumber', label: '房间号', required: true },
                { key: 'type', label: '房型', required: true },
                { key: 'price', label: '价格', type: 'number', required: true },
                { key: 'capacity', label: '可住人数', type: 'number', default: 2 },
                { key: 'hotelId', label: '所属酒店ID', type: 'number', default: 1 },
                { key: 'description', label: '描述' },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ]
        },
        booking: {
            title: '预订管理',
            apiBase: '/cloud-booking/api/bookings',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'customerName', label: '客户名' },
                { key: 'customerPhone', label: '电话' },
                { key: 'checkInDate', label: '入住日期' },
                { key: 'checkOutDate', label: '离店日期' },
                { key: 'totalAmount', label: '总金额' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'order') },
                { key: 'createTime', label: '创建时间' }
            ],
            fields: [
                { key: 'customerName', label: '客户名', required: true },
                { key: 'customerPhone', label: '电话', required: true },
                { key: 'hotelId', label: '酒店ID', type: 'number', default: 1 },
                { key: 'roomId', label: '房间ID', type: 'number', default: 1 },
                { key: 'checkInDate', label: '入住日期', required: true },
                { key: 'checkOutDate', label: '离店日期', required: true },
                { key: 'totalAmount', label: '总金额', type: 'number', required: true },
                { key: 'status', label: '状态', type: 'select', options: ORDER_STATUS, default: 'PAID' }
            ]
        },
        customer: {
            title: '客户管理',
            apiBase: '/cloud-customer/api/customers',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'name', label: '姓名' },
                { key: 'phone', label: '手机号' },
                { key: 'gender', label: '性别' },
                { key: 'membership', label: '会员等级' },
                { key: 'registerTime', label: '注册时间' },
                { key: 'createTime', label: '创建时间' }
            ],
            fields: [
                { key: 'name', label: '姓名', required: true },
                { key: 'phone', label: '手机号', required: true },
                { key: 'idCard', label: '身份证号' },
                { key: 'gender', label: '性别', type: 'select', options: [
                    { value: '男', label: '男' },
                    { value: '女', label: '女' }
                ], default: '男' },
                { key: 'email', label: '邮箱' },
                { key: 'membership', label: '会员等级', type: 'select', options: [
                    { value: '普通会员', label: '普通会员' },
                    { value: '白银会员', label: '白银会员' },
                    { value: '黄金会员', label: '黄金会员' },
                    { value: '钻石会员', label: '钻石会员' }
                ], default: '普通会员' }
            ]
        },
        seckillMgmt: {
            title: '秒杀活动管理',
            apiBase: '/cloud-business/api/seckill',
            idField: 'id',
            columns: [
                { key: 'id', label: 'ID', width: '60px' },
                { key: 'seckillName', label: '活动名称' },
                { key: 'productCode', label: '商品编码' },
                { key: 'seckillPrice', label: '秒杀价', render: (r) => '¥' + r.seckillPrice },
                { key: 'soldCount', label: '已售', width: '70px' },
                { key: 'seckillStock', label: '剩余库存', width: '80px' },
                { key: 'limitCount', label: '限购', width: '60px' },
                { key: 'startTime', label: '开始时间' },
                { key: 'endTime', label: '结束时间' },
                { key: 'status', label: '状态', renderHtml: (r) => {
                    if (r.status === 1) return '<span class="badge-pill badge-ok">进行中</span>';
                    if (r.status === 2) return '<span class="badge-pill badge-muted">已结束</span>';
                    return '<span class="badge-pill badge-warn">未发布</span>';
                }}
            ],
            fields: [
                { key: 'seckillName', label: '活动名称', required: true },
                { key: 'productCode', label: '商品编码', required: true },
                { key: 'seckillPrice', label: '秒杀价', type: 'number', required: true },
                { key: 'seckillStock', label: '秒杀库存', type: 'number', required: true },
                { key: 'limitCount', label: '每人限购', type: 'number', required: true, default: 1 },
                { key: 'startTime', label: '开始时间', type: 'datetime-local', required: true },
                { key: 'endTime', label: '结束时间', type: 'datetime-local', required: true },
                { key: 'status', label: '状态', type: 'select', options: [
                    { value: '0', label: '未发布' },
                    { value: '1', label: '进行中' },
                    { value: '2', label: '已结束' }
                ], default: '0' }
            ]
        },
        hotelAmenity: {
            title: '酒店信息管理',
            apiBase: '/cloud-business/api/hotel-amenities',
            idField: 'id',
            pagination: true,
            batchDelete: true,
            columns: [
                { key: 'hotelName', label: '酒店名称' },
                { key: 'address', label: '地址' },
                { key: 'starRating', label: '星级', renderHtml: (r) => '⭐'.repeat(r.starRating || 0) },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') },
                { key: 'amenities', label: '待遇', renderHtml: (r) => {
                    if (!r.amenities || !r.amenities.length) return '—';
                    return r.amenities.slice(0, 3).map(function (a) { return '<span class="badge-pill badge-info">' + escapeHtml(a) + '</span>'; }).join(' ') +
                        (r.amenities.length > 3 ? ' <span class="badge-pill badge-muted">+'+ (r.amenities.length-3) +'</span>' : '');
                }}
            ],
            fields: [
                { key: 'hotelName', label: '酒店名称', required: true },
                { key: 'address', label: '地址' },
                { key: 'phone', label: '电话' },
                { key: 'starRating', label: '星级', type: 'number', default: 3 },
                { key: 'description', label: '描述' },
                { key: 'amenities', label: '待遇列表（一行一个）', type: 'textarea' },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ],
            detailRenderHtml: function(r) {
                var stars = '';
                for (var i = 0; i < (r.starRating || 0); i++) stars += '⭐';
                var amenitiesHtml = '';
                if (r.amenities && r.amenities.length) {
                    r.amenities.forEach(function(a) { amenitiesHtml += '<span class="badge-pill badge-info" style="margin:0.15rem">' + escapeHtml(a) + '</span>'; });
                } else {
                    amenitiesHtml = '<span style="color:var(--muted)">暂无</span>';
                }
                return '<div style="padding:1rem">' +
                    '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem">' +
                    '<h2 style="margin:0;font-size:1.3rem">' + escapeHtml(r.hotelName) + '</h2>' +
                    '<span style="font-size:1.1rem">' + stars + '</span>' +
                    '</div>' +
                    '<div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem;margin-bottom:1rem;padding:0.75rem;background:var(--bg);border-radius:8px">' +
                    '<div><div style="font-size:0.75rem;color:var(--muted)">📍 地址</div><div>' + escapeHtml(r.address || '—') + '</div></div>' +
                    '<div><div style="font-size:0.75rem;color:var(--muted)">📞 电话</div><div>' + escapeHtml(r.phone || '—') + '</div></div>' +
                    '</div>' +
                    (r.description ? '<div style="margin-bottom:1rem;padding:0.75rem;background:var(--bg);border-radius:8px"><div style="font-size:0.75rem;color:var(--muted);margin-bottom:0.25rem">📝 描述</div><div>' + escapeHtml(r.description) + '</div></div>' : '') +
                    '<div style="padding:0.75rem;background:var(--bg);border-radius:8px"><div style="font-size:0.75rem;color:var(--muted);margin-bottom:0.5rem">🎯 酒店待遇</div><div>' + amenitiesHtml + '</div></div>' +
                    '</div>';
            }
        },
        restaurantAmenity: {
            title: '饭店信息管理',
            apiBase: '/cloud-business/api/restaurant-amenities',
            idField: 'id',
            pagination: true,
            batchDelete: true,
            columns: [
                { key: 'id', label: 'ID', width: '80px' },
                { key: 'restaurantName', label: '饭店名称' },
                { key: 'cuisineType', label: '菜系', renderHtml: (r) => '<span class="badge-pill badge-info">' + escapeHtml(r.cuisineType || '—') + '</span>' },
                { key: 'rating', label: '评分', renderHtml: (r) => '⭐'.repeat(r.rating || 0) },
                { key: 'address', label: '地址' },
                { key: 'status', label: '状态', renderHtml: (r) => statusBadge(r.status, 'status') },
                { key: 'amenities', label: '特色标签', renderHtml: (r) => {
                    if (!r.amenities) return '—';
                    var tags = r.amenities.split(',');
                    return tags.slice(0, 3).map(function (a) { return '<span class="badge-pill badge-info">' + escapeHtml(a.trim()) + '</span>'; }).join(' ') +
                        (tags.length > 3 ? ' <span class="badge-pill badge-muted">+'+ (tags.length-3) +'</span>' : '');
                }}
            ],
            fields: [
                { key: 'restaurantName', label: '饭店名称', required: true },
                { key: 'cuisineType', label: '菜系类型' },
                { key: 'rating', label: '评分', type: 'number', default: 3 },
                { key: 'address', label: '地址' },
                { key: 'phone', label: '电话' },
                { key: 'description', label: '描述' },
                { key: 'amenities', label: '特色标签（逗号分隔）' },
                { key: 'status', label: '状态', type: 'select', options: STATUS_OPTIONS, default: '1' }
            ],
            detailRenderHtml: function(r) {
                var stars = '';
                for (var i = 0; i < (r.rating || 0); i++) stars += '⭐';
                var amenityTags = '';
                if (r.amenities) {
                    r.amenities.split(',').forEach(function(a) { amenityTags += '<span class="badge-pill badge-info" style="margin:0.15rem">' + escapeHtml(a.trim()) + '</span>'; });
                } else {
                    amenityTags = '<span style="color:var(--muted)">暂无</span>';
                }
                return '<div style="padding:1rem">' +
                    '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem">' +
                    '<h2 style="margin:0;font-size:1.3rem">' + escapeHtml(r.restaurantName) + '</h2>' +
                    '<div><span class="badge-pill badge-info" style="font-size:0.9rem">' + escapeHtml(r.cuisineType || '—') + '</span>' + stars + '</div>' +
                    '</div>' +
                    '<div style="display:grid;grid-template-columns:1fr 1fr;gap:0.75rem;margin-bottom:1rem;padding:0.75rem;background:var(--bg);border-radius:8px">' +
                    '<div><div style="font-size:0.75rem;color:var(--muted)">📍 地址</div><div>' + escapeHtml(r.address || '—') + '</div></div>' +
                    '<div><div style="font-size:0.75rem;color:var(--muted)">📞 电话</div><div>' + escapeHtml(r.phone || '—') + '</div></div>' +
                    '</div>' +
                    (r.description ? '<div style="margin-bottom:1rem;padding:0.75rem;background:var(--bg);border-radius:8px"><div style="font-size:0.75rem;color:var(--muted);margin-bottom:0.25rem">📝 描述</div><div>' + escapeHtml(r.description) + '</div></div>' : '') +
                    '<div style="padding:0.75rem;background:var(--bg);border-radius:8px"><div style="font-size:0.75rem;color:var(--muted);margin-bottom:0.5rem">🏷️ 特色标签</div><div>' + amenityTags + '</div></div>' +
                    '</div>';
            }
        }
    };

    class ApiStore {
        constructor(config) {
            this.config = config;
            this.apiBase = config.apiBase;
            this.rows = [];
            this.pageNo = 1;
            this.pageSize = 10;
            this.total = 0;
            this.pages = 1;
            this.storagePath = '';
        }

        async fetchStoragePath() {
            try {
                const res = await apiRequest('GET', API_STORAGE_PATH, null, true);
                if (res.code === 200 && res.data) {
                    this.storagePath = res.data;
                }
            } catch (e) { /* ignore */ }
        }

        async load(pageNo, pageSize, keyword) {
            let url = this.apiBase;
            const params = [];
            if (this.config.pagination) {
                const p = pageNo || this.pageNo;
                const s = pageSize || this.pageSize;
                params.push('page=' + p, 'size=' + s);
            }
            if (keyword) {
                params.push('keyword=' + encodeURIComponent(keyword));
            }
            if (params.length) url += '?' + params.join('&');
            const res = await apiRequest('GET', url, null, true);
            if (res.code !== 200) {
                throw new Error(res.message || '加载失败');
            }
            const d = res.data;
            if (this.config.pagination) {
                this.rows = d.records || [];
                this.pageNo = d.pageNo;
                this.pageSize = d.pageSize;
                this.total = d.total;
                this.pages = Math.ceil(this.total / this.pageSize) || 1;
            } else {
                this.rows = d || [];
            }
            return this.rows;
        }

        list() {
            return this.rows.slice();
        }

        get(id) {
            return this.rows.find((r) => String(r.id) === String(id));
        }

        async create(row) {
            const res = await apiRequest('POST', this.apiBase, row, true);
            if (res.code !== 200) {
                throw new Error(res.message || '新增失败');
            }
            await this.load(this.config.pagination ? 1 : undefined);
            return res.data;
        }

        async update(id, row) {
            const res = await apiRequest('PUT', this.apiBase + '/' + id, row, true);
            if (res.code !== 200) {
                throw new Error(res.message || '更新失败');
            }
            await this.load();
            return res.data;
        }

        async remove(id) {
            const res = await apiRequest('DELETE', this.apiBase + '/' + id, null, true);
            if (res.code !== 200) {
                throw new Error(res.message || '删除失败');
            }
            await this.load();
            return true;
        }

        async reset() {
            const res = await apiRequest('POST', this.apiBase + '/reset', null, true);
            if (res.code !== 200) {
                throw new Error(res.message || '重置失败');
            }
            if (this.config.pagination) {
                await this.load(1);
            } else {
                this.rows = res.data || [];
            }
            return this.rows;
        }
    }

    class MockCrudPanel {
        constructor(key, config) {
            this.key = key;
            this.config = config;
            this.store = new ApiStore(config);
            this.editingId = null;
            this.keyword = '';
            this.selectedIds = new Set();
            this.loading = false;
        }

        async render(root) {
            this.root = root;
            root.innerHTML = '<div class="crud-panel"><div class="crud-skeleton"></div><div class="crud-loading"><div class="crud-loading-spinner"></div>加载中…</div></div>';
            root.innerHTML = '<div class="crud-panel"><div class="crud-skeleton"></div><div class="crud-loading"><div class="crud-loading-spinner"></div>加载中…</div></div>';
            root.innerHTML = '<div class="crud-panel"><div class="crud-skeleton"></div><div class="crud-loading"><div class="crud-loading-spinner"></div>加载中…</div></div>';
            root.innerHTML = '<div class="crud-panel"><div class="crud-skeleton"></div><div class="crud-loading"><div class="crud-loading-spinner"></div>加载中…</div></div>';
            try {
                await this.store.fetchStoragePath();
                await this.store.load();
            } catch (e) {
                root.innerHTML = '<p class="hint" style="color:#f87171">' + escapeHtml(e.message) + '</p>';
                return;
            }
            root.innerHTML = '';
            const panel = document.createElement('div');
            panel.className = 'crud-panel';
            panel.appendChild(this.buildToolbar());
            panel.appendChild(this.buildTableWrap());
            if (this.config.pagination) {
                this.paginationEl = this.buildPagination();
                panel.appendChild(this.paginationEl);
            }
            panel.appendChild(this.buildModal());
            root.appendChild(panel);
            this.refreshTable();
        }

        buildToolbar() {
            const bar = document.createElement('div');
            bar.className = 'crud-toolbar';
            bar.innerHTML =
                '<div class="crud-toolbar-left">' +
                '<h2>' + this.config.title + '</h2>' +
                '<span class="crud-count" data-total></span>' +
                '</div>' +
                '<div class="crud-toolbar-right">' +
                '<div class="search-wrap"><span class="search-icon" aria-hidden="true">⌕</span>' +
                '<input type="search" class="crud-search" placeholder="搜索关键字…" autocomplete="off" />' +
                '</div>' +
                '<button type="button" class="btn btn-ghost btn-sm" data-act="refresh">↻ 刷新</button>' +
                '<button type="button" class="btn btn-sm" data-act="add">＋ 新增</button>' +
                '<button type="button" class="btn btn-ghost btn-sm" data-act="reset">重置数据</button>' +
                '</div>';
            // 批量删除按钮（仅在配置了 batchDelete 时显示）
            if (this.config.batchDelete) {
                const rightDiv = bar.querySelector('.crud-toolbar-right');
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'btn btn-ghost btn-sm';
                btn.disabled = true;
                btn.textContent = '☐ 批量删除';
                btn.dataset.act = 'batch-del';
                rightDiv.insertBefore(btn, rightDiv.querySelector('[data-act="refresh"]'));
                this.batchDelBtn = btn;
                btn.onclick = () => this.batchDelete();
            }
            const search = bar.querySelector('.crud-search');
            let searchTimer;
            search.addEventListener('input', (e) => {
                this.keyword = e.target.value.trim();
                clearTimeout(searchTimer);
                searchTimer = setTimeout(() => {
                    this.store.load(this.config.pagination ? 1 : undefined, undefined, this.keyword || undefined)
                        .then(() => this.refreshTable())
                        .catch((e) => toast(e.message || '搜索失败', false));
                }, 300);
            });
            this.countEl = bar.querySelector('[data-total]');
            bar.querySelector('[data-act="add"]').onclick = () => this.openModal();
            bar.querySelector('[data-act="refresh"]').onclick = async () => {
                const btn = bar.querySelector('[data-act="refresh"]');
                btn.disabled = true;
                try {
                    await this.store.load();
                    this.refreshTable();
                    toast('数据已刷新', true);
                } catch (e) {
                    toast(e.message || '刷新失败', false);
                } finally {
                    btn.disabled = false;
                }
            };
            bar.querySelector('[data-act="reset"]').onclick = async () => {
                if (!confirm('确定恢复为初始种子数据？')) return;
                try {
                    await this.store.reset();
                    this.refreshTable();
                } catch (e) {
                    toast(e.message || '重置失败');
                }
            };
            return bar;
        }

        buildTableWrap() {
            const wrap = document.createElement('div');
            wrap.className = 'table-wrap card';
            const scroll = document.createElement('div');
            scroll.className = 'table-scroll';
            scroll.innerHTML = '<table class="data-table"><thead></thead><tbody></tbody></table>';
            wrap.appendChild(scroll);
            this.tableHead = scroll.querySelector('thead');
            this.tableBody = scroll.querySelector('tbody');
            return wrap;
        }

        buildModal() {
            const overlay = document.createElement('div');
            overlay.className = 'modal-overlay';
            overlay.hidden = true;
            const formId = 'crud-form-' + this.key;
            overlay.innerHTML =
                '<div class="modal">' +
                '<div class="modal-header"><h3 class="modal-title">编辑</h3>' +
                '<button type="button" class="modal-close" aria-label="关闭">&times;</button></div>' +
                '<form id="' + formId + '" class="modal-form"></form>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-ghost" data-act="cancel">取消</button>' +
                '<button type="submit" class="btn" data-act="save" form="' + formId + '">保存</button>' +
                '</div></div>';
            overlay.querySelector('.modal-close').onclick = () => this.closeModal();
            overlay.querySelector('[data-act="cancel"]').onclick = () => this.closeModal();
            this.formEl = overlay.querySelector('form');
            this.formEl.onsubmit = (e) => {
                e.preventDefault();
                this.saveForm();
            };
            overlay.querySelector('[data-act="save"]').onclick = (e) => {
                e.preventDefault();
                if (this.formEl.reportValidity()) {
                    this.saveForm();
                }
            };
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) this.closeModal();
            });
            this.modal = overlay;
            this.modalTitle = overlay.querySelector('.modal-title');
            this.saveBtn = overlay.querySelector('[data-act="save"]');
            return overlay;
        }

        /** 构建分页条容器 */
        buildPagination() {
            const div = document.createElement('div');
            div.className = 'crud-pagination';
            return div;
        }

        /** 刷新分页条状态（只显示当前页前后几页 + 首尾 + 省略号） */
        refreshPagination() {
            const s = this.store;
            if (!this.config.pagination || s.pages <= 1) {
                this.paginationEl.innerHTML = '';
                return;
            }
            let html = '<div class="page-info">共 ' + s.total + ' 条，第 ' + s.pageNo + '/' + s.pages + ' 页</div><div class="page-btns">';
            html += '<button type="button" class="btn btn-ghost btn-sm" data-page="prev"' + (s.pageNo <= 1 ? ' disabled' : '') + '>‹ 上一页</button>';

            // 生成页码范围：始终显示第1页和最后1页，当前页前后各显示5页
            const range = 5;
            const pages = [];
            pages.push(1);
            let start = Math.max(2, s.pageNo - range);
            let end = Math.min(s.pages - 1, s.pageNo + range);
            if (start > 2) pages.push('...');
            for (let i = start; i <= end; i++) pages.push(i);
            if (end < s.pages - 1) pages.push('...');
            if (s.pages > 1) pages.push(s.pages);

            pages.forEach(function (p) {
                if (p === '...') {
                    html += '<span style="padding:0 0.3rem;color:var(--muted)">…</span>';
                } else if (p === s.pageNo) {
                    html += '<button type="button" class="btn btn-sm" disabled>' + p + '</button>';
                } else {
                    html += '<button type="button" class="btn btn-ghost btn-sm" data-page="' + p + '">' + p + '</button>';
                }
            });

            html += '<button type="button" class="btn btn-ghost btn-sm" data-page="next"' + (s.pageNo >= s.pages ? ' disabled' : '') + '>下一页 ›</button>';
            html += '</div>';
            this.paginationEl.innerHTML = html;
            const self = this;
            this.paginationEl.querySelectorAll('[data-page]').forEach(function (btn) {
                btn.onclick = async function () {
                    let p = btn.getAttribute('data-page');
                    if (p === 'prev') p = s.pageNo - 1;
                    else if (p === 'next') p = s.pageNo + 1;
                    else p = parseInt(p);
                    if (p < 1 || p > s.pages) return;
                    self.goToPage(p);
                };
            });
        }

        /** 跳转到指定页 */
        async goToPage(pageNo) {
            try {
                await this.store.load(pageNo);
                this.refreshTable();
            } catch (e) {
                toast(e.message || '加载失败', false);
            }
        }

        filteredRows() {
            const rows = this.store.list();
            if (!this.keyword) return rows;
            const kw = this.keyword;
            return rows.filter((r) => {
                return Object.keys(r).some((k) => String(r[k]).toLowerCase().indexOf(kw) >= 0);
            });
        }

        refreshTable() {
            this.selectedIds.clear();
            const cols = this.config.columns;
            const hasBatch = this.config.batchDelete;
            let headHtml = '<tr>';
            if (hasBatch) {
                headHtml += '<th style="width:44px"><input type="checkbox" id="selAll-' + this.key + '" title="全选/取消"></th>';
            }
            cols.forEach((c) => {
                headHtml += '<th' + (c.width ? ' style="width:' + c.width + '"' : '') + '>' + c.label + '</th>';
            });
            headHtml += '<th style="width:140px">操作</th></tr>';
            this.tableHead.innerHTML = headHtml;

            const all = this.store.list();
            const rows = this.filteredRows();
            if (this.countEl) {
                if (this.config.pagination) {
                    this.countEl.textContent = '共 ' + this.store.total + ' 条，第 ' + this.store.pageNo + '/' + this.store.pages + ' 页';
                } else {
                    this.countEl.textContent = '共 ' + all.length + ' 条';
                }
            }
            const colspan = cols.length + 1 + (hasBatch ? 1 : 0);
            if (!rows.length) {
                const msg = this.keyword ? '没有匹配的记录' : '暂无数据，点击「新增」创建记录';
                this.tableBody.innerHTML = '<tr><td colspan="' + colspan + '" class="empty-cell"><span class="empty-state-icon">📭</span>' + escapeHtml(msg) + '</td></tr>';
                return;
            }
            let bodyHtml = '';
            const self = this;
            rows.forEach((r) => {
                bodyHtml += '<tr>';
                if (hasBatch) {
                    const checked = self.selectedIds.has(String(r.id)) ? ' checked' : '';
                    bodyHtml += '<td><input type="checkbox" class="chk-row" value="' + r.id + '"' + checked + '></td>';
                }
                cols.forEach((c) => {
                    let cell;
                    if (c.renderHtml) cell = c.renderHtml(r);
                    else if (c.render) cell = escapeHtml(String(c.render(r)));
                    else cell = escapeHtml(r[c.key] != null ? String(r[c.key]) : '—');
                    bodyHtml += '<td>' + cell + '</td>';
                });
                bodyHtml += '<td class="actions">' +
                    '<button type="button" class="btn-link" data-edit="' + r.id + '">编辑</button>' +
                    '<button type="button" class="btn-link" data-detail="' + r.id + '">详情</button>' +
                    '<button type="button" class="btn-link danger" data-del="' + r.id + '">删除</button>' +
                    '</td></tr>';
            });
            this.tableBody.innerHTML = bodyHtml;
            this.tableBody.querySelectorAll('[data-edit]').forEach((btn) => {
                btn.onclick = () => self.openModal(btn.getAttribute('data-edit'));
            });
            this.tableBody.querySelectorAll('[data-del]').forEach((btn) => {
                btn.onclick = async () => {
                    const id = btn.getAttribute('data-del');
                    if (!confirm('确定删除该记录？')) return;
                    try {
                        await self.store.remove(id);
                        self.refreshTable();
                        toast('已删除', true);
                    } catch (e) {
                        toast(e.message || '删除失败', false);
                    }
                };
            });
            this.tableBody.querySelectorAll('[data-detail]').forEach((btn) => {
                btn.onclick = async () => {
                    const id = btn.getAttribute('data-detail');
                    try {
                        const res = await apiRequest('GET', self.config.apiBase + '/' + id, null, true);
                        if (res.code !== 200) {
                            toast(res.message || '查询失败', false);
                            return;
                        }
                        if (self.detailModal) self.detailModal.remove();
                        const overlay = document.createElement('div');
                        overlay.className = 'modal-overlay';
                        let contentHtml;
                        if (self.config.detailRenderHtml) {
                            contentHtml = self.config.detailRenderHtml(res.data);
                        } else {
                            const json = JSON.stringify(res.data, null, 2);
                            contentHtml = '<pre style="padding:1rem;background:var(--bg);border-radius:6px;overflow:auto;font-size:0.8rem;white-space:pre-wrap;max-height:400px">' +
                                escapeHtml(json) + '</pre>';
                        }
                        overlay.innerHTML =
                            '<div class="modal" style="max-width:600px">' +
                            '<div class="modal-header"><h3 class="modal-title">' + (res.data.hotelName ? escapeHtml(res.data.hotelName) : '详情') + '</h3>' +
                            '<button type="button" class="modal-close" aria-label="关闭">&times;</button></div>' +
                            '<div class="modal-form">' + contentHtml + '</div></div>';
                        overlay.querySelector('.modal-close').onclick = () => {
                            document.body.classList.remove('modal-open');
                            overlay.remove();
                        };
                        overlay.onclick = (e) => {
                            if (e.target === overlay) {
                                document.body.classList.remove('modal-open');
                                overlay.remove();
                            }
                        };
                        document.body.appendChild(overlay);
                        document.body.classList.add('modal-open');
                        requestAnimationFrame(() => overlay.classList.add('is-visible'));
                        self.detailModal = overlay;
                    } catch (e) {
                        toast(e.message || '查询失败', false);
                    }
                };
            });

            // 批量删除复选框事件
            if (hasBatch) {
                const selAll = document.getElementById('selAll-' + this.key);
                if (selAll) {
                    selAll.onclick = () => {
                        const checked = selAll.checked;
                        document.querySelectorAll('#crud-' + this.key + ' .chk-row').forEach(cb => {
                            cb.checked = checked;
                            const id = String(cb.value);
                            if (checked) self.selectedIds.add(id);
                            else self.selectedIds.delete(id);
                        });
                        self.updateBatchDelBtn();
                    };
                }
                document.querySelectorAll('#crud-' + this.key + ' .chk-row').forEach(cb => {
                    cb.onclick = () => {
                        const id = String(cb.value);
                        if (cb.checked) self.selectedIds.add(id);
                        else self.selectedIds.delete(id);
                        // 同步全选框状态
                        const all = document.querySelectorAll('#crud-' + this.key + ' .chk-row');
                        const checked = document.querySelectorAll('#crud-' + this.key + ' .chk-row:checked');
                        if (selAll) selAll.checked = all.length > 0 && all.length === checked.length;
                        self.updateBatchDelBtn();
                    };
                });
                self.updateBatchDelBtn();
            }

            // 刷新分页控件
            if (this.paginationEl) this.refreshPagination();
        }

        openModal(id) {
            this.editingId = id || null;
            const isEdit = !!id;
            const row = isEdit ? this.store.get(id) : {};
            this.modalTitle.textContent = (isEdit ? '编辑' : '新增') + ' - ' + this.config.title;
            let html = '';
            this.config.fields.forEach((f) => {
                let val = row[f.key] != null ? row[f.key] : (f.default != null ? f.default : '');
                if (f.type === 'datetime-local' && val) {
                    val = String(val).substring(0, 16);
                }
                html += '<label>' + f.label + (f.required ? ' *' : '') + '</label>';
                if (f.type === 'select') {
                    html += '<select name="' + f.key + '"' + (f.required ? ' required' : '') + '>';
                    f.options.forEach((o) => {
                        html += '<option value="' + escapeAttr(o.value) + '"' +
                            (String(val) === String(o.value) ? ' selected' : '') + '>' + escapeHtml(o.label) + '</option>';
                    });
                    html += '</select>';
                } else if (f.type === 'textarea') {
                    const textVal = Array.isArray(val) ? val.join('\n') : String(val);
                    html += '<textarea name="' + f.key + '" rows="4"' + (f.required ? ' required' : '') + '>' + escapeHtml(textVal) + '</textarea>';
                } else {
                    const type = f.type || 'text';
                    html += '<input name="' + f.key + '" type="' + type + '" value="' + escapeAttr(val) + '"' +
                        (f.required ? ' required' : '') + '/>';
                }
            });
            this.formEl.innerHTML = html;
            this.modal.hidden = false;
            document.body.classList.add('modal-open');
            requestAnimationFrame(() => this.modal.classList.add('is-visible'));
        }

        closeModal() {
            this.modal.classList.remove('is-visible');
            document.body.classList.remove('modal-open');
            this.modal.hidden = true;
            this.editingId = null;
        }

        collectFormData() {
            const data = {};
            this.config.fields.forEach((f) => {
                const el = this.formEl.querySelector('[name="' + f.key + '"]');
                let val = el ? el.value : '';
                if (f.type === 'number' && val !== '') {
                    val = Number(val);
                }
                if (f.type === 'datetime-local' && val !== '') {
                    val = val + ':00';
                }
                if (f.type === 'textarea' && val !== '') {
                    val = val.split('\n').map(s => s.trim()).filter(Boolean);
                }
                data[f.key] = val;
            });
            return data;
        }

        async saveForm() {
            if (!this.formEl.reportValidity()) {
                return;
            }
            const data = this.collectFormData();
            const btn = this.saveBtn;
            const oldText = btn ? btn.textContent : '';
            if (btn) {
                btn.disabled = true;
                btn.textContent = '保存中…';
            }
            try {
                if (this.editingId) {
                    await this.store.update(this.editingId, data);
                    toast('保存成功', true);
                } else {
                    await this.store.create(data);
                    toast('新增成功', true);
                }
                this.closeModal();
                this.refreshTable();
            } catch (e) {
                toast(e.message || '保存失败', false);
            } finally {
                if (btn) {
                    btn.disabled = false;
                    btn.textContent = oldText;
                }
            }
        }

        /** 更新批量删除按钮状态 */
        updateBatchDelBtn() {
            if (this.batchDelBtn) {
                const size = this.selectedIds.size;
                this.batchDelBtn.disabled = size === 0;
                this.batchDelBtn.textContent = '☐ 批量删除' + (size ? ' (' + size + ')' : '');
            }
        }

        /** 批量删除 */
        async batchDelete() {
            if (this.selectedIds.size === 0) return;
            if (!confirm('确定删除选中的 ' + this.selectedIds.size + ' 条记录？')) return;
            const btn = this.batchDelBtn;
            if (btn) btn.disabled = true;
            try {
                const ids = Array.from(this.selectedIds).join(',');
                const res = await apiRequest('DELETE', this.config.apiBase + '/batch?ids=' + ids, null, true);
                if (res.code !== 200) {
                    toast(res.message || '批量删除失败', false);
                    return;
                }
                this.selectedIds.clear();
                await this.store.load();
                this.refreshTable();
                toast('已批量删除 ' + ids.split(',').length + ' 条', true);
            } catch (e) {
                toast(e.message || '批量删除失败', false);
            } finally {
                if (btn) btn.disabled = false;
            }
        }
    }

    function escapeHtml(s) {
        return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    function escapeAttr(s) {
        return escapeHtml(String(s)).replace(/'/g, '&#39;');
    }

    const instances = {};

    return {
        mount(key, rootEl) {
            if (!CONFIGS[key]) return;
            if (!instances[key]) {
                instances[key] = new MockCrudPanel(key, CONFIGS[key]);
            }
            instances[key].render(rootEl);
        },
        refresh(key) {
            if (instances[key] && instances[key].root) {
                instances[key].store.load().then(function () {
                    instances[key].refreshTable();
                }).catch(function (e) {
                    toast(e.message || '刷新失败');
                });
            }
        }
    };
})();
