document.addEventListener('DOMContentLoaded', () => {
    const table = document.querySelector('#listaAlumnos');
    if (!table) {
        return;
    }

    table.addEventListener('click', async (event) => {
        const link = event.target.closest('a');

        const isDelete = link.classList.contains('borrarAlumnoLink');
            if (!isDelete) {
                return;
        }
    event.preventDefault();

    const tr = link.closest('tr');
    const idEl = tr && tr.querySelector('.alumnoId');
    const id = idEl ? idEl.textContent.trim() : null;

    const url = "/admin/alumnos/" + id + "/delete/confirm";

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`Response status: ${response.status}`);
        const html = await response.text();
        document.querySelector('#placeholder-modal').innerHTML = html;

        const modalEl = document.querySelector('#delete-modal');
        if (modalEl) {
            const modal = new bootstrap.Modal(modalEl);
            modal.show();
        } else {
            console.error('Modal no encontrado en el HTML recibido.');
        }
    } catch (error) {
        console.error('Error al cargar el modal de confirmación:', error);
        }
    });
});

const buscador = document.querySelector('#buscador');
buscador.addEventListener('keyup', async () => {
    const url = "/admin/alumnos/filter?";
    const queryParams = new URLSearchParams({nombre: buscador.value}).toString();
    try {
        const response = await fetch(url + queryParams);
        if (!response.ok) throw new Error(`Response status: ${response.status}`);

        const html = await response.text();
        document.querySelector('#listaAlumnos').innerHTML = html;
    } catch (error) {
        console.error(error.message);
    }
});
