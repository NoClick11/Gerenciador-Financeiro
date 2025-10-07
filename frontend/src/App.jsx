import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { authenticatedFetch } from './services/api';
import './App.css';

import BalanceDisplay from './components/BalanceDisplay';

function App() {
  const navigate = useNavigate();
  
  const [transactions, setTransactions] = useState([]);
  const [currentDate, setCurrentDate] = useState(new Date());
  const [aiSuggestion, setAiSuggestion] = useState(null);
  const [isLoadingAI, setIsLoadingAI] = useState(false);

  const [currentUser, setCurrentUser] = useState(null);

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth() + 1;
  const token = localStorage.getItem('jwt-token');

  useEffect(() => {
    if (!token) {
      navigate('/login');
      return;
    }

    const fetchInitialData = async () => {
      try {
        const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/transactions/by-month?year=${year}&month=${month}`;
        const response = await authenticatedFetch(apiUrl);
        if (response.ok) {
          setTransactions(await response.json());
        } else {
          setTransactions([]);
        }
      } catch (error) {
        console.error("Erro ao buscar transações:", error);
      }
      try {
        const userApiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/users/me`;
        const response = await authenticatedFetch(userApiUrl);
        if (response.ok) setCurrentUser(await response.json());
      } catch (error) { console.error("Erro ao buscar dados do usuário:", error); }
    };

    fetchInitialData();
  }, [year, month, navigate, token]);

  const handleTransactionAdded = (newTransaction) => {
    const [tYear, tMonth] = newTransaction.date.split('-').map(Number);
    if (tYear === year && tMonth === month) {
      setTransactions(current => [...current, newTransaction]);
    }
  };

  const handleDelete = async (transactionToDelete) => {
    const deleteLogic = async (url) => {
        const response = await authenticatedFetch(url, { method: 'DELETE' });
        if (response.ok) {
            setTransactions(current => current.filter(t => t.id !== transactionToDelete.id));
        } else {
            throw new Error('Falha ao deletar a transação');
        }
    };

    if (transactionToDelete.recurrenceType === 'RECURRING') {
        const stopRecurrence = window.confirm(
            `"${transactionToDelete.description}" é um item recorrente.\n\n- Clique em 'OK' para Excluir Permanentemente.\n- Clique em 'Cancelar' para Excluir Apenas Deste Mês.`
        );
        if (stopRecurrence) {
            try {
                const descParam = encodeURIComponent(transactionToDelete.description);
                const recurringApiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/recurring-transactions/by-description?description=${descParam}`;
                await authenticatedFetch(recurringApiUrl, { method: 'DELETE' });
                await deleteLogic(`${import.meta.env.VITE_API_BASE_URL}/api/transactions/${transactionToDelete.id}`);
                alert("Recorrência cancelada com sucesso!");
            } catch (error) {
                console.error("Erro ao cancelar recorrência:", error);
            }
        } else {
            await deleteLogic(`${import.meta.env.VITE_API_BASE_URL}/api/transactions/${transactionToDelete.id}`);
        }
    } else {
        if (window.confirm("Tem certeza que deseja deletar esta transação?")) {
            await deleteLogic(`${import.meta.env.VITE_API_BASE_URL}/api/transactions/${transactionToDelete.id}`);
        }
    }
  };

  const handleGenerateSuggestion = async () => {
    setIsLoadingAI(true);
    setAiSuggestion(null);
    try {
      const apiUrl = `${import.meta.env.VITE_API_BASE_URL}/api/ai/suggestions`;
      const response = await authenticatedFetch(apiUrl, { method: 'POST' });
      if (!response.ok) throw new Error('Falha ao gerar sugestão');
      setAiSuggestion(await response.json());
    } catch (error) {
      console.error("Erro ao gerar sugestão:", error);
    } finally {
      setIsLoadingAI(false);
    }
  };

  const handlePreviousMonth = () => {
    setCurrentDate(prev => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(prev => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt-token');
    navigate('/login');
    window.location.reload();
  };
  
  const totalIncome = transactions.filter(t => t.type === 'INCOME').reduce((acc, t) => acc + parseFloat(t.amount), 0);
  const totalExpense = transactions.filter(t => t.type === 'EXPENSE').reduce((acc, t) => acc + parseFloat(t.amount), 0);
  const balance = totalIncome - totalExpense;

  return (
    <div className="main-container">
      <nav className="navbar">
        <div>
          {token && <Link to="/">Dashboard</Link>}
        </div>
        
        {token && (
          <BalanceDisplay income={totalIncome} expense={totalExpense} balance={balance} />
        )}

        <div>
          {token ? (<button onClick={handleLogout}>Sair</button>) : (<Link to="/login">Login</Link>)}
        </div>
      </nav>

      <main>
        {/* AQUI ESTÁ A CORREÇÃO: A propriedade 'context' passando todos os dados */}
        <Outlet context={{ transactions, handleTransactionAdded, handleDelete, totalIncome, totalExpense, currentDate, handlePreviousMonth, handleNextMonth, aiSuggestion, isLoadingAI, handleGenerateSuggestion, setAiSuggestion, currentUser }} />
      </main>
    </div>
  );
}

export default App;