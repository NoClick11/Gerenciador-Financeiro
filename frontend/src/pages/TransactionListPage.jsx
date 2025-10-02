import { useState, useEffect } from 'react';
import TransactionForm from '../components/TransactionForm';
import BalanceDisplay from '../components/BalanceDisplay';
import TransactionChart from '../components/TransactionChart';

function TransactionListPage() {
  const [transactions, setTransactions] = useState([]);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/transactions');
        const data = await response.json();
        setTransactions(data);
      } catch (error) {
        console.error("Erro ao buscar transações:", error);
      }
    };

    fetchTransactions();
  }, []); 

  const handleDelete = async (id) => {
    try {
      const response = await fetch(`http://localhost:8080/api/transactions/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        setTransactions(currentTransactions =>
          currentTransactions.filter(transaction => transaction.id !== id)
        );
      } else {
        throw new Error('Falha ao deletar a transação');
      }
    } catch (error) {
      console.error("Houve um erro ao deletar a transação:", error);
    }
  };
  
  const handleTransactionAdded = (newTransaction) => {
    setTransactions(currentTransactions => [...currentTransactions, newTransaction]);
  };

  const totalIncome = transactions
    .filter(t => t.type === 'INCOME')
    .reduce((acc, transaction) => acc + parseFloat(transaction.amount), 0);

  const totalExpense = transactions
    .filter(t => t.type === 'EXPENSE')
    .reduce((acc, transaction) => acc + parseFloat(transaction.amount), 0);

  const balance = totalIncome - totalExpense;

  return (
    <div style={{ fontFamily: 'sans-serif', padding: '20px', maxWidth: '800px', margin: 'auto' }}>
      <h1>Gerenciador Financeiro</h1>

      {}
      <div style={{ display: 'flex', justifyContent: 'center', gap: '50px', alignItems: 'center', flexWrap: 'wrap', marginBottom: '20px', padding: '20px', background: '#f9f9f9', borderRadius: '8px' }}>
        <BalanceDisplay income={totalIncome} expense={totalExpense} balance={balance} />
        <TransactionChart income={totalIncome} expense={totalExpense} />
      </div>
      
      {}
      <TransactionForm onTransactionAdded={handleTransactionAdded} />
      
      <hr style={{ margin: '30px 0' }}/>

      {}
      <h2>Histórico de Transações</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {transactions.map(transaction => (
          <li key={transaction.id} style={{ background: '#fff', border: '1px solid #ddd', borderRadius: '5px', padding: '10px 15px', marginBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <span>{transaction.date}</span> |
              <span style={{ margin: '0 10px' }}> {transaction.description} </span> |
              <span style={{ color: transaction.type === 'INCOME' ? 'green' : 'red', fontWeight: 'bold', textTransform: 'uppercase' }}> {transaction.type} </span> |
              <strong> R$ {parseFloat(transaction.amount).toFixed(2)}</strong>
            </div>
            <button onClick={() => handleDelete(transaction.id)} style={{ background: '#ff4d4d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', padding: '5px 10px' }}>
              X
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionListPage;