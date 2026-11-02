import { useEffect, useState } from 'react';
import { api } from './services/api';

interface Transaction {
  id: string;
  amount: number;
  type: 'CREDIT' | 'DEBIT';
  status: string;
  createdAt: string;
}

export default function App() {
  const [balance, setBalance] = useState<number | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [amount, setAmount] = useState('');
  const accountId = "c6808574-6c8b-46c0-9114-ba0bf7a88bf2"; 

  const fetchData = async () => {
    try {
      const balanceRes = await api.get(`/accounts/${accountId}`);
      setBalance(balanceRes.data.balance);
      
      const txRes = await api.get(`/transactions?accountId=${accountId}`);
      setTransactions(txRes.data);
    } catch (error) {
      console.error("Erro ao buscar dados:", error);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const handleTransaction = async (type: 'CREDIT' | 'DEBIT') => {
    if (!amount || isNaN(Number(amount))) return;
    try {
      await api.post('/transactions', {
        accountId,
        amount: Number(amount),
        type,
        idempotencyKey: crypto.randomUUID()
      });
      setAmount('');
      setTimeout(fetchData, 500);
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

        <div className="mt-8 border-t border-gray-200 pt-6">
          <h2 className="text-lg font-bold text-gray-800 mb-4">Histórico Recente</h2>
          <div className="space-y-3 max-h-64 overflow-y-auto">
            {transactions.length > 0 ? transactions.map(tx => (
              <div key={tx.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg border border-gray-100">
                <div>
                  <p className="text-sm font-semibold text-gray-700">{tx.type === 'CREDIT' ? 'Depósito' : 'Saque'}</p>
                  <p className="text-xs text-gray-500">{new Date(tx.createdAt).toLocaleString('pt-BR')}</p>
                </div>
                <span className={`font-bold ${tx.type === 'CREDIT' ? 'text-green-600' : 'text-red-600'}`}>
                  {tx.type === 'CREDIT' ? '+' : '-'} R$ {tx.amount.toFixed(2)}
                </span>
              </div>
            )) : (
              <p className="text-sm text-gray-500 text-center">Nenhuma transação encontrada.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}