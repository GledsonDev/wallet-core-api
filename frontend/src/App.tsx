import { useEffect, useState } from 'react';
import { api } from './services/api';

export default function App() {
  const [balance, setBalance] = useState<number | null>(null);
  const [amount, setAmount] = useState('');
  const accountId = "c6808574-6c8b-46c0-9114-ba0bf7a88bf2"; 

  const fetchBalance = () => {
    api.get(`/accounts/${accountId}`)
      .then(response => setBalance(response.data.balance))
      .catch(error => console.error("Erro:", error));
  };

  useEffect(() => { fetchBalance(); }, []);

  const handleTransaction = async (type: 'CREDIT' | 'DEBIT') => {
    if (!amount || isNaN(Number(amount))) return;
    try {
      await api.post('/transactions', {
        accountId,
        amount: Number(amount),
        type,
        idempotencyKey: crypto.randomUUID() // Gera chave única nativamente
      });
      setAmount('');
      setTimeout(fetchBalance, 500); // Dá tempo ao RabbitMQ para processar
    } catch (error) {
      alert("Erro ao processar transação. Verifique o console.");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Wallet Back-Office</h1>
        
        <div className="bg-blue-50 p-4 rounded-lg border border-blue-100 mb-6">
          <p className="text-sm text-blue-600 font-semibold uppercase">Saldo Atual</p>
          <p className="text-3xl font-bold text-blue-900">
            {balance !== null ? `R$ ${balance.toFixed(2)}` : 'Carregando...'}
          </p>
        </div>

        <div className="space-y-4">
          <input 
            type="number" 
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="Digite o valor (ex: 50.00)"
            className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
          />
          <div className="flex gap-4">
            <button onClick={() => handleTransaction('CREDIT')} className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-3 rounded-lg transition">
              Depositar
            </button>
            <button onClick={() => handleTransaction('DEBIT')} className="w-full bg-red-600 hover:bg-red-700 text-white font-bold py-3 rounded-lg transition">
              Sacar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}