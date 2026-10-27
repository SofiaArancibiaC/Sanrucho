
// src/components/test/BadgeOferta.tsx
//es el molde visual que verán los usuarios en la aplicación.
interface BadgeOfertaProps {
texto: string;
descuento?: number;
}

export default function BadgeOferta({ texto, descuento }: BadgeOfertaProps) {
return (
    <span data-testid="badge-oferta" className="badge-oferta">
    {texto} {descuento ? `-${descuento}%` : ""}
    </span>
);
}

//(el componente visual).
// Crea una pequeña etiqueta o insignia (badge) reutilizable para mostrar ofertas o promociones en una página web.
//BadgeOfertaProps): Pide un texto obligatorio (texto) y un descuento opcional (descuento?