import { useState, useEffect } from 'react';

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

  return (
    <div>
      <h2>Lista de Transações</h2>
      <ul>
        {}
        {transactions.map(transaction => (
          <li key={transaction.id}>
            {transaction.date} | {transaction.description} | {transaction.type} | R$ {transaction.amount}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionListPage;