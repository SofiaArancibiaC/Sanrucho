// Stub de SweetAlert2 para las pruebas (no se muestran ventanas emergentes).
const Swal = {
  fire: (_opciones?: unknown) => Promise.resolve({ isConfirmed: true, value: true }),
};

export default Swal;
