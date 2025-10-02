
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

function TransactionChart({ income, expense }) {
  const data = {
    labels: ['Entradas', 'Saídas'],
    datasets: [
      {
        label: 'Valor em R$',
        data: [income, expense],
        backgroundColor: [
          'rgba(75, 192, 192, 0.6)',
          'rgba(255, 99, 132, 0.6)', 
        ],
        borderColor: [
          'rgba(75, 192, 192, 1)',
          'rgba(255, 99, 132, 1)',
        ],
        borderWidth: 1,
      },
    ],
  };

  return (
    <div style={{ maxWidth: '300px', margin: 'auto' }}>
      <h3>Visão Geral Gráfica</h3>
      <Doughnut data={data} />
    </div>
  );
}

export default TransactionChart;