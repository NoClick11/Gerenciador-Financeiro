import { useState, useEffect } from "react";
import TransactionForm from "../components/TransactionForm";
import BalanceDisplay from "../components/BalanceDisplay";
import TransactionChart from "../components/TransactionChart";
import { authenticatedFetch } from "../services/api";

function TransactionListPage() {
  const [transactions, setTransactions] = useState([]);
  const [aiSuggestion, setAiSuggestion] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // Efeito que roda na primeira vez que a página carrega
  useEffect(() => {
    // Função para buscar as transações
    const fetchTransactions = async () => {
      try {
        const response = await authenticatedFetch(
          "http://localhost:8080/api/transactions"
        );
        if (response.ok) {
          const data = await response.json();
          setTransactions(data);
        } else {
          // Se o token for inválido/expirado, o backend retornará um erro
          console.error("Falha ao autenticar para buscar transações.");
        }
      } catch (error) {
        console.error("Erro ao buscar transações:", error);
      }
    };

    // Função para buscar a última sugestão da IA
    const fetchLatestSuggestion = async () => {
      try {
        const response = await authenticatedFetch(
          "http://localhost:8080/api/ai/suggestions"
        );
        if (response.ok) {
          const data = await response.json();
          setAiSuggestion(data);
        } else {
          console.log("Nenhuma sugestão salva encontrada.");
        }
      } catch (error) {
        console.error("Erro ao buscar sugestão salva:", error);
      }
    };

    fetchTransactions();
    fetchLatestSuggestion();
  }, []); // Array vazio garante que rode apenas uma vez

  // Função para lidar com a deleção de uma transação
  const handleDelete = async (id) => {
    try {
      const response = await authenticatedFetch(
        `http://localhost:8080/api/transactions/${id}`,
        {
          method: "DELETE",
        }
      );
      if (response.ok) {
        setTransactions((currentTransactions) =>
          currentTransactions.filter((transaction) => transaction.id !== id)
        );
      } else {
        throw new Error("Falha ao deletar a transação");
      }
    } catch (error) {
      console.error("Houve um erro ao deletar a transação:", error);
    }
  };

  // Função para atualizar a lista após uma nova transação ser adicionada
  const handleTransactionAdded = (newTransaction) => {
    setTransactions((currentTransactions) => [
      ...currentTransactions,
      newTransaction,
    ]);
  };

  // Função para GERAR uma NOVA sugestão
  const handleGenerateSuggestion = async () => {
    setIsLoading(true);
    setAiSuggestion(null); // Limpa a sugestão antiga da tela
    try {
      const response = await authenticatedFetch(
        "http://localhost:8080/api/ai/suggestions",
        {
          method: "POST",
        }
      );
      if (!response.ok) {
        throw new Error("Falha ao gerar nova sugestão");
      }

      const newSuggestion = await response.json();
      setAiSuggestion(newSuggestion); // Atualiza a tela com a nova sugestão
    } catch (error) {
      console.error("Erro ao gerar sugestão:", error);
      setAiSuggestion({
        suggestionText:
          "Desculpe, não foi possível gerar sugestões no momento. Tente novamente mais tarde.",
      });
    } finally {
      setIsLoading(false);
    }
  };

  // --- LÓGICA DE CÁLCULO ---
  const totalIncome = transactions
    .filter((t) => t.type === "INCOME")
    .reduce((acc, t) => acc + parseFloat(t.amount), 0);
  const totalExpense = transactions
    .filter((t) => t.type === "EXPENSE")
    .reduce((acc, t) => acc + parseFloat(t.amount), 0);
  const balance = totalIncome - totalExpense;

  // --- RENDERIZAÇÃO ---
  return (
    <div
      style={{
        fontFamily: "sans-serif",
        padding: "20px",
        maxWidth: "800px",
        margin: "auto",
      }}
    >
      <h1>Gerenciador Financeiro com IA</h1>

      <div
        style={{
          display: "flex",
          justifyContent: "center",
          gap: "50px",
          alignItems: "center",
          flexWrap: "wrap",
          marginBottom: "20px",
          padding: "20px",
          background: "#f9f9f9",
          borderRadius: "8px",
        }}
      >
        <BalanceDisplay
          income={totalIncome}
          expense={totalExpense}
          balance={balance}
        />
        <TransactionChart income={totalIncome} expense={totalExpense} />
      </div>

      <TransactionForm onTransactionAdded={handleTransactionAdded} />
      <hr style={{ margin: "30px 0" }} />

      <div style={{ marginBottom: "30px" }}>
        <h2>Análise com IA</h2>
        <button onClick={handleGenerateSuggestion} disabled={isLoading}>
          {isLoading ? "Analisando..." : "Gerar Nova Análise"}
        </button>

        {isLoading && (
          <p style={{ color: "#007bff" }}>
            Aguarde, o assistente Gemini está pensando...
          </p>
        )}

        {aiSuggestion && !isLoading && (
          <div
            style={{
              marginTop: "15px",
              padding: "15px",
              border: "1px solid #007bff",
              borderRadius: "5px",
              background: "#f0f8ff",
            }}
          >
            <h3>
              Sugestão do Assistente{" "}
              {aiSuggestion.createdAt &&
                `(de ${new Date(aiSuggestion.createdAt).toLocaleString()})`}
              :
            </h3>
            <pre
              style={{
                whiteSpace: "pre-wrap",
                fontFamily: "inherit",
                fontSize: "1em",
              }}
            >
              {aiSuggestion.suggestionText}
            </pre>
            <button
              onClick={() => setAiSuggestion(null)}
              style={{ marginTop: "10px" }}
            >
              Limpar
            </button>
          </div>
        )}
      </div>

      <hr style={{ margin: "30px 0" }} />

      <h2>Histórico de Transações</h2>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {transactions.map((transaction) => (
          <li
            key={transaction.id}
            style={{
              background: "#fff",
              border: "1px solid #ddd",
              borderRadius: "5px",
              padding: "10px 15px",
              marginBottom: "10px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <div>
              <span>{transaction.date}</span> |
              <span style={{ margin: "0 10px" }}>
                {" "}
                {transaction.description}{" "}
              </span>{" "}
              |
              <span
                style={{
                  color: transaction.type === "INCOME" ? "green" : "red",
                  fontWeight: "bold",
                  textTransform: "uppercase",
                }}
              >
                {" "}
                {transaction.type}{" "}
              </span>{" "}
              |<strong> R$ {parseFloat(transaction.amount).toFixed(2)}</strong>
            </div>
            <button
              onClick={() => handleDelete(transaction.id)}
              style={{
                background: "#ff4d4d",
                color: "white",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer",
                padding: "5px 10px",
              }}
            >
              X
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionListPage;
