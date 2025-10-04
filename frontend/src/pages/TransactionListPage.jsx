import { useState, useEffect } from 'react';
import TransactionForm from '../components/TransactionForm';
import BalanceDisplay from '../components/BalanceDisplay';
import TransactionChart from '../components/TransactionChart';
import { authenticatedFetch } from '../services/api';

function TransactionListPage() {
  // --- ESTADOS (STATE) ---
const [transactions, setTransactions] = useState([]);
  const [aiSuggestion, setAiSuggestion] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [currentDate, setCurrentDate] = useState(new Date());

  // Variáveis derivadas do estado de data
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth() + 1;

  // --- EFEITOS (EFFECTS) ---
  // Efeito para buscar as transações do mês (roda sempre que o mês/ano muda)
  useEffect(() => {
    const fetchTransactionsForMonth = async () => {
      try {
        const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/transactions/by-month?year=${year}&month=${month}`;
        const response = await authenticatedFetch(apiUrl);
        if (response.ok) {
          const data = await response.json();
          setTransactions(data);
        } else {
          setTransactions([]);
        }
      } catch (error) {
        console.error("Erro ao buscar transações do mês:", error);
      }
    };
    fetchTransactionsForMonth();
  }, [year, month]);

  // Efeito para buscar a última sugestão salva (roda apenas uma vez)
  useEffect(() => {
    const fetchLatestSuggestion = async () => {
      try {
        const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/ai/suggestions`;
        const response = await authenticatedFetch(apiUrl);
        if (response.ok) {
          const data = await response.json();
          setAiSuggestion(data);
        }
      } catch (error) {
        console.error("Erro ao buscar sugestão salva:", error);
      }
    };
    fetchLatestSuggestion();
  }, []);

  const handleTransactionAdded = (newTransaction) => {
    const [transactionYear, transactionMonth] = newTransaction.date.split('-').map(Number);
    if (transactionYear === year && transactionMonth === month) {
      setTransactions(currentTransactions => [...currentTransactions, newTransaction]);
    } else {
      alert("Transação adicionada com sucesso (para um mês diferente do que está sendo exibido).");
    }
  };

    const handleDelete = async (transactionToDelete) => {
    // Primeiro, verifica se a transação é do tipo recorrente
    if (transactionToDelete.recurrenceType === 'RECURRING') {
      
      const stopRecurrence = window.confirm(
        `"${transactionToDelete.description}" é um item recorrente.\n\n- Clique em 'OK' para Excluir Permanentemente (para este e os próximos meses).\n- Clique em 'Cancelar' para Excluir Apenas Deste Mês.`
      );

      if (stopRecurrence) {
        // --- LÓGICA PARA EXCLUIR PERMANENTEMENTE ---
        try {
          // 1. Deleta o "modelo" recorrente para que não seja mais gerado
          const descriptionParam = encodeURIComponent(transactionToDelete.description);
          const recurringApiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/recurring-transactions/by-description?description=${descriptionParam}`;
          await authenticatedFetch(recurringApiUrl, { method: 'DELETE' });

          // 2. Deleta a "instância" da transação do mês atual
          const transactionApiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/transactions/${transactionToDelete.id}`;
          const response = await authenticatedFetch(transactionApiUrl, { method: 'DELETE' });
          
          if (response.ok) {
            setTransactions(current => current.filter(t => t.id !== transactionToDelete.id));
            alert("Recorrência cancelada com sucesso!");
          } else {
            throw new Error("Falha ao deletar a transação deste mês.");
          }
        } catch (error) {
          console.error("Erro ao cancelar recorrência:", error);
        }

      } else {
        // --- LÓGICA PARA EXCLUIR APENAS ESTE MÊS ---
        try {
          const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/transactions/${transactionToDelete.id}`;
          const response = await authenticatedFetch(apiUrl, { method: 'DELETE' });
          if (response.ok) {
            setTransactions(current => current.filter(t => t.id !== transactionToDelete.id));
          }
        } catch (error) {
          console.error("Erro ao deletar transação:", error);
        }
      }

    } else {
      // --- LÓGICA PARA ITENS NÃO RECORRENTES (ÚNICOS) ---
      const isConfirmed = window.confirm("Tem certeza que deseja deletar esta transação?");
      if (isConfirmed) {
        try {
          const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/transactions/${transactionToDelete.id}`;
          const response = await authenticatedFetch(apiUrl, { method: 'DELETE' });
          if (response.ok) {
            setTransactions(current => current.filter(t => t.id !== transactionToDelete.id));
          }
        } catch (error) {
          console.error("Erro ao deletar transação:", error);
        }
      }
    }
  };

  const handleGenerateSuggestion = async () => {
    setIsLoading(true);
    setAiSuggestion(null);
    try {
      const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/ai/suggestions`;
      const response = await authenticatedFetch(apiUrl, { method: 'POST' });
      if (!response.ok) throw new Error('Falha ao gerar nova sugestão');
      const newSuggestion = await response.json();
      setAiSuggestion(newSuggestion);
    } catch (error) {
      console.error("Erro ao gerar sugestão:", error);
      setAiSuggestion({ suggestionText: 'Desculpe, não foi possível gerar sugestões no momento.' });
    } finally {
      setIsLoading(false);
    }
  };

  const handlePreviousMonth = () => {
    setCurrentDate(prevDate => new Date(prevDate.getFullYear(), prevDate.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(prevDate => new Date(prevDate.getFullYear(), prevDate.getMonth() + 1, 1));
  };

  // --- CÁLCULOS DERIVADOS ---
  const totalIncome = transactions.filter(t => t.type === 'INCOME').reduce((acc, t) => acc + parseFloat(t.amount), 0);
  const totalExpense = transactions.filter(t => t.type === 'EXPENSE').reduce((acc, t) => acc + parseFloat(t.amount), 0);
  const balance = totalIncome - totalExpense;

  // --- RENDERIZAÇÃO (JSX) ---
  return (
    <div style={{ fontFamily: 'sans-serif', padding: '20px', maxWidth: '800px', margin: 'auto' }}>
      <h1>Gerenciador Financeiro com IA</h1>

      <div style={{ display: 'flex', justifyContent: 'center', gap: '50px', alignItems: 'center', flexWrap: 'wrap', marginBottom: '20px', padding: '20px', background: '#f9f9f9', borderRadius: '8px' }}>
        <BalanceDisplay income={totalIncome} expense={totalExpense} balance={balance} />
        <TransactionChart income={totalIncome} expense={totalExpense} />
      </div>
      
      <TransactionForm onTransactionAdded={handleTransactionAdded} />
      
      <hr style={{ margin: '30px 0' }}/>
      
      <div style={{ marginBottom: '30px' }}>
        <h2>Análise com IA</h2>
        <button onClick={handleGenerateSuggestion} disabled={isLoading}>
          {isLoading ? 'Analisando...' : 'Gerar Nova Análise'}
        </button>
        {isLoading && <p style={{ color: '#007bff' }}>Aguarde, o assistente Gemini está pensando...</p>}
        {aiSuggestion && !isLoading && (
          <div style={{ marginTop: '15px', padding: '15px', border: '1px solid #007bff', borderRadius: '5px', background: '#f0f8ff' }}>
            <h3>Sugestão do Assistente {aiSuggestion.createdAt && `(de ${new Date(aiSuggestion.createdAt).toLocaleString()})`}:</h3>
            <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit', fontSize: '1em' }}>{aiSuggestion.suggestionText}</pre>
            <button onClick={() => setAiSuggestion(null)} style={{ marginTop: '10px' }}>Limpar</button>
          </div>
        )}
      </div>

      <hr style={{ margin: '30px 0' }}/>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Histórico de Transações</h2>
        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
          <button onClick={handlePreviousMonth}>&lt; Mês Anterior</button>
          <span style={{ fontWeight: 'bold', minWidth: '70px', textAlign: 'center' }}>
            {String(month).padStart(2, '0')}/{year}
          </span>
          <button onClick={handleNextMonth}>Próximo Mês &gt;</button>
        </div>
      </div>
      
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {transactions.map(transaction => (
          <li key={transaction.id} style={{ background: '#fff', border: '1px solid #ddd', borderRadius: '5px', padding: '10px 15px', marginBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <span>{transaction.date}</span> |
              <span style={{ margin: '0 10px' }}> {transaction.description} </span> |
              <span style={{ color: transaction.type === 'INCOME' ? 'green' : 'red', fontWeight: 'bold', textTransform: 'uppercase' }}> {transaction.type} </span> |
              <strong> R$ {parseFloat(transaction.amount).toFixed(2)}</strong>
            </div>
            <button onClick={() => handleDelete(transaction)} style={{ background: '#ff4d4d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', padding: '5px 10px' }}>
              X
            </button>
          </li>
        ))}
      </ul>
    </div>
  );  
}

export default TransactionListPage;