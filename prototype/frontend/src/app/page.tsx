import Link from "next/link";

export default function HomePage() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-8">
      <h1 className="text-4xl font-bold mb-4">SIFAP 2.0</h1>
      <p className="text-lg text-gray-600 mb-8">
        Sistema de Fiscalização e Administração de Pagamentos
      </p>
      <nav className="flex gap-4">
        <Link
          href="/beneficiaries"
          className="rounded-lg bg-blue-600 px-6 py-3 text-white hover:bg-blue-700"
        >
          Beneficiários
        </Link>
        <Link
          href="/payments"
          className="rounded-lg bg-green-600 px-6 py-3 text-white hover:bg-green-700"
        >
          Pagamentos
        </Link>
        <Link
          href="/audit"
          className="rounded-lg bg-gray-600 px-6 py-3 text-white hover:bg-gray-700"
        >
          Auditoria
        </Link>
      </nav>
    </main>
  );
}


