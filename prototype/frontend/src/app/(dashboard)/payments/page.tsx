import { apiFetch } from "@/lib/api";

interface Payment {
  id: number;
  cpf: string;
  competence: string;
  status: string;
  type: string;
  grossValue: number;
  netValue: number;
  totalDiscounts: number;
}

export default async function PaymentsPage() {
  let payments: Payment[] = [];
  try {
    payments = await apiFetch<Payment[]>("/api/v1/payments?status=G");
  } catch {
    // API might not be available during build
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-6">Pagamentos</h1>
      <div className="overflow-x-auto">
        <table className="min-w-full border border-gray-200 rounded-lg">
          <thead className="bg-gray-100">
            <tr>
              <th className="px-4 py-2 text-left">ID</th>
              <th className="px-4 py-2 text-left">Competência</th>
              <th className="px-4 py-2 text-left">Status</th>
              <th className="px-4 py-2 text-left">Tipo</th>
              <th className="px-4 py-2 text-right">Bruto</th>
              <th className="px-4 py-2 text-right">Descontos</th>
              <th className="px-4 py-2 text-right">Líquido</th>
            </tr>
          </thead>
          <tbody>
            {payments.map((p) => (
              <tr key={p.id} className="border-t">
                <td className="px-4 py-2">{p.id}</td>
                <td className="px-4 py-2">{p.competence}</td>
                <td className="px-4 py-2">
                  <span className={`px-2 py-1 rounded text-sm ${
                    p.status === "G" ? "bg-yellow-100 text-yellow-800" :
                    p.status === "P" ? "bg-green-100 text-green-800" :
                    "bg-red-100 text-red-800"
                  }`}>
                    {p.status}
                  </span>
                </td>
                <td className="px-4 py-2">{p.type === "N" ? "Normal" : "Dezembro"}</td>
                <td className="px-4 py-2 text-right">R$ {p.grossValue?.toFixed(2)}</td>
                <td className="px-4 py-2 text-right">R$ {p.totalDiscounts?.toFixed(2)}</td>
                <td className="px-4 py-2 text-right font-semibold">R$ {p.netValue?.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {payments.length === 0 && (
          <p className="text-gray-500 mt-4">Nenhum pagamento encontrado.</p>
        )}
      </div>
    </div>
  );
}


