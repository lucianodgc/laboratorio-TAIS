const $ = s => document.querySelector(s);
const esc = v => String(v ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const money = n => Number(n).toLocaleString('es-UY', {minimumFractionDigits: 2, maximumFractionDigits: 2});
const parseDate = s => new Date(String(s).replace(/(\.\d{3})\d+/, '$1'));
const fmtDate = s => s ? parseDate(s).toLocaleString('es-UY') : '';

async function api(path) {
    const r = await fetch(path);
    if (!r.ok) {
        let msg = r.status + ' ' + r.statusText;
        try { const b = await r.json(); if (b.mensaje) msg = b.mensaje; } catch (e) {}
        throw new Error(msg);
    }
    return r.json();
}

function openModal(html) {
    $('#box').innerHTML = '<button class="close" onclick="closeModal()">×</button>' + html;
    $('#modal').classList.add('open');
}
function closeModal() { $('#modal').classList.remove('open'); }
$('#modal').addEventListener('click', e => { if (e.target.id === 'modal') closeModal(); });

async function showOrdenes() {
    $('#view').innerHTML = 'Cargando…';
    try {
        const data = await api('/api/ordenes');
        data.sort((a, b) => parseDate(b.fechaCreacion) - parseDate(a.fechaCreacion)); // más reciente primero
        $('#view').innerHTML =
            '<table><thead><tr><th>Id</th><th>Email</th><th>Teléfono</th><th>Status</th><th>Fecha creación</th></tr></thead><tbody>' +
            data.map(o => `<tr data-id="${esc(o.id)}"><td>${esc(o.id)}</td><td>${esc(o.email)}</td><td>${esc(o.telefono)}</td>
        <td><span class="badge ${esc(o.estado)}">${esc(o.estado)}</span></td><td>${fmtDate(o.fechaCreacion)}</td></tr>`).join('') +
            '</tbody></table>' + (data.length ? '' : '<p>No hay órdenes.</p>');
        document.querySelectorAll('#view tbody tr').forEach(tr => tr.onclick = () => showOrden(tr.dataset.id));
    } catch (e) { $('#view').innerHTML = `<p class="err">Error: ${esc(e.message)}</p>`; }
}

async function showOrden(id) {
    try {
        const d = await api(`/api/ordenes/${id}/detalle`);
        let html = `<h2>Orden ${esc(d.id)}</h2>
      <p><b>Estado:</b> <span class="badge ${esc(d.estado)}">${esc(d.estado)}</span><br>
      <b>Dirección:</b> ${esc(d.direccion)}<br><b>Fecha:</b> ${fmtDate(d.fechaCreacion)}</p>
      <h3>Productos</h3>
      <table><thead><tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th></tr></thead><tbody>` +
            d.items.map(i => `<tr><td>${esc(i.producto.nombre)}</td><td>${esc(i.cantidad)}</td><td>${money(i.producto.precio)}</td></tr>`).join('') +
            '</tbody></table>';

        if (d.estado === 'READY_TO_DELIVERY') {
            try {
                const f = await api(`/api/ordenes/${id}/factura`);
                const nombres = Object.fromEntries(d.items.map(i => [i.producto.id, i.producto.nombre]));
                html += `<h3>Factura</h3>
          <table><thead><tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th></tr></thead><tbody>` +
                    f.items.map(i => `<tr><td>${esc(nombres[i.productoId] || i.productoId)}</td><td>${esc(i.cantidad)}</td>
            <td>${money(i.precioUnitario)}</td><td>${money(i.subtotal)}</td></tr>`).join('') +
                    `</tbody></table><div class="total">Total: ${money(f.montoTotal)}</div>`;
            } catch (e) { html += `<p class="err">No se pudo cargar la factura: ${esc(e.message)}</p>`; }
        }
        openModal(html);
    } catch (e) { openModal(`<p class="err">Error: ${esc(e.message)}</p>`); }
}

async function showProductos() {
    $('#view').innerHTML = 'Cargando…';
    try {
        const data = await api('/api/productos');
        $('#view').innerHTML =
            '<table><thead><tr><th>Id</th><th>Nombre</th><th>Descripción</th><th>Precio unitario</th><th>Stock</th></tr></thead><tbody>' +
            data.map(p => `<tr data-id="${esc(p.id)}"><td>${esc(p.id)}</td><td>${esc(p.nombre)}</td><td>${esc(p.descripcion)}</td>
        <td>${money(p.precio)}</td><td>${esc(p.stock)}</td></tr>`).join('') +
            '</tbody></table>' + (data.length ? '' : '<p>No hay productos.</p>');
        document.querySelectorAll('#view tbody tr').forEach(tr =>
            tr.onclick = () => showProducto(data.find(p => p.id === tr.dataset.id)));
    } catch (e) { $('#view').innerHTML = `<p class="err">Error: ${esc(e.message)}</p>`; }
}

function showProducto(p) {
    const imgs = (p.imagenes || []).map(u =>
        `<img src="${esc(u)}" alt="${esc(p.nombre)}" onerror="this.style.display='none'">`).join('');
    openModal(`<h2>${esc(p.nombre)}</h2><p>${esc(p.descripcion)}</p>
    <div class="imgs">${imgs || '<p>Este producto no tiene imágenes asociadas.</p>'}</div>`);
}

function setTab(t) {
    ['ordenes', 'productos'].forEach(x => $('#tab-' + x).classList.toggle('active', x === t));
    t === 'ordenes' ? showOrdenes() : showProductos();
}
$('#tab-ordenes').onclick = () => setTab('ordenes');
$('#tab-productos').onclick = () => setTab('productos');
setTab('ordenes');