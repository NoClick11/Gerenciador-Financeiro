import { useState, useEffect } from 'react';
import TransactionForm from '../components/TransactionForm';

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
        console.log(`Transação com id ${id} deletada com sucesso!`);
      } else {
        throw new Error('Falha ao deletar a transação');
      }
    } catch (error) {
      console.error("Houve um erro ao deletar a transação:", error);
    }
  };

  return (
    <div>
      <h1>Gerenciador Financeiro</h1>
      <TransactionForm />
      <hr />
      <h2>Lista de Transações</h2>
      <ul>
        {transactions.map(transaction => (
          <li key={transaction.id}>
            <span>{transaction.date}</span> |
            <span> {transaction.description} </span> |
            <span>{transaction.type}</span> |
            <strong> R$ {transaction.amount}</strong>
            {}
            {}
            <button onClick={() => handleDelete(transaction.id)} style={{ marginLeft: '10px' }}>
              X
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionListPage;