
// src/components/test/BotonContador.tsx
//
import { useState } from "react";

export default function BotonContador() {
const [contador, setContador] = useState<number>(0);

const incrementar = () => {
    setContador((prev) => prev + 1);
};

return (
    <div>
    <span data-testid="contador-valor">{contador}</span>
    <button data-testid="btn-incrementar" onClick={incrementar}>
        Incrementar
    </button>
    </div>
);
}
