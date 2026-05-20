import { apiFetch } from "@/lib/api";

interface Beneficiary {
  id: number;
  cpf: string;
  name: string;
  status: string;
  regionCode: number;
  numDependents: number;
}

export default async function BeneficiariesPage() {
  let beneficiaries: Beneficiary[] = [];
  try {
    beneficiaries = await apiFetch<Beneficiary[]>("/api/v1/beneficiaries/by-program/1");
  } catch {
    // API might not be available during build
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-6">Beneficiários</h1>
      <div className="overflow-x-auto">
        <table className="min-w-full border border-gray-200 rounded-lg">
          <thead className="bg-gray-100">
            <tr>
              <th className="px-4 py-2 text-left">ID</th>
              <th className="px-4 py-2 text-left">Nome</th>
              <th className="px-4 py-2 text-left">Status</th>
              <th className="px-4 py-2 text-left">Região</th>
              <th className="px-4 py-2 text-left">Dependentes</th>
            </tr>
          </thead>
          <tbody>
            {beneficiaries.map((b) => (
              <tr key={b.id} className="border-t">
                <td className="px-4 py-2">{b.id}</td>
                <td className="px-4 py-2">{b.name}</td>
                <td className="px-4 py-2">
                  <span className={`px-2 py-1 rounded text-sm ${b.status === "A" ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"}`}>
                    {b.status}
                  </span>
                </td>
                <td className="px-4 py-2">{b.regionCode}</td>
                <td className="px-4 py-2">{b.numDependents}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {beneficiaries.length === 0 && (
          <p className="text-gray-500 mt-4">Nenhum beneficiário encontrado.</p>
        )}
      </div>
    </div>
  );
}


