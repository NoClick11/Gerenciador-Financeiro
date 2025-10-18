import { useOutletContext } from "react-router-dom";
import TransactionForm from "../components/TransactionForm";
import TransactionChart from "../components/TransactionChart";

function TransactionListPage() {
  const today = new Date();
  const currentRealYear = today.getFullYear();
  const currentRealMonth = today.getMonth() + 1;
  const {
    transactions,
    handleTransactionAdded,
    handleDelete,
    totalIncome,
    totalExpense,
    currentDate,
    handlePreviousMonth,
    handleNextMonth,
    aiSuggestion,
    isLoadingAI,
    handleGenerateSuggestion,
    setAiSuggestion,
    currentUser,
  } = useOutletContext();

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth() + 1;

  let canNavigateBack = false;
  if (currentUser && currentUser.createdAt) {
    const registrationDate = new Date(currentUser.createdAt);
    const registrationYear = registrationDate.getFullYear();
    const registrationMonth = registrationDate.getMonth() + 1;

    canNavigateBack =
      year > registrationYear ||
      (year === registrationYear && month > registrationMonth);
  }

return (
    <div>
      {}
      <div className="transaction-list-page-top-section">
        <TransactionChart income={totalIncome} expense={totalExpense} />
      </div>

      <TransactionForm onTransactionAdded={handleTransactionAdded} />
      <hr className="page-divider" /> {}

      {}
      <div className="ai-suggestion-section">
        <h2>Análise com IA</h2>
        <button onClick={handleGenerateSuggestion} disabled={isLoadingAI}>
          {isLoadingAI ? "Analisando..." : "Gerar Nova Análise"}
        </button>
        {isLoadingAI && <p className="ai-loading-message">Aguarde...</p>} {}
        {aiSuggestion && !isLoadingAI && (
          <div className="ai-suggestion-box"> {}
            <h3>
              Sugestão do Assistente{" "}
              {aiSuggestion.createdAt &&
                `(de ${new Date(aiSuggestion.createdAt).toLocaleString()})`}
              :
            </h3>
            <pre className="ai-suggestion-text"> {}
              {aiSuggestion.suggestionText}
            </pre>
            <button
              className="ai-suggestion-clear-button" 
              onClick={() => setAiSuggestion(null)}
            >
              Limpar
            </button>
          </div>
        )}
      </div>

      <hr className="page-divider" /> {}

      {}
      <div className="transaction-history-header"> {}
        <h2>Histórico de Transações</h2>
        {}
        <div className="transaction-history-nav"> {}
          <button onClick={handlePreviousMonth} disabled={!canNavigateBack}>
            &lt; Mês Anterior
          </button>
          <span className="transaction-history-date"> {}
            {String(month).padStart(2, "0")}/{year}
          </span>
          <button
            onClick={handleNextMonth}
            disabled={year === currentRealYear && month === currentRealMonth}
          >
            Próximo Mês &gt;
          </button>
        </div>
      </div>

      {}
      {transactions.length === 0 && (
        <div className="no-transactions-message"> {}
          <p>Nenhuma transação encontrada para este mês.</p>
        </div>
      )}

      {}
      <ul className="transaction-list">
        {transactions.map((transaction) => (
          <li
            key={transaction.id}
            className={`transaction-item ${
              transaction.type === "INCOME"
                ? "transaction-item--income"
                : "transaction-item--expense"
            }`}
          >
            <div className="transaction-item-details">
              <span>{transaction.date}</span>
              <span>{transaction.description}</span>
              <strong>
                {transaction.type === "INCOME" ? "+" : "-"} R${" "}
                {parseFloat(transaction.amount).toFixed(2)}
              </strong>
            </div>
            <button onClick={() => handleDelete(transaction)}>X</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
export default TransactionListPage;
