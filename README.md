# 🚗 Locadora de Veículos (Aluguel de Carros)

Aplicativo Android de gestão de locadoras de veículos desenvolvido com **Jetpack Compose**, **Material 3** e **Room**.

## 📱 Funcionalidades

- **Dashboard** com faturamento total, resumo da frota (disponíveis, alugados, manutenção), locações ativas e ações rápidas.
- **Veículos**: cadastro, edição, exclusão, busca por marca/modelo/placa e filtro por status.
- **Clientes**: cadastro, edição, exclusão e busca por nome, CPF ou CNH.
- **Locações**: criação de contratos (selecionando veículo disponível + cliente + diárias), conclusão e cancelamento — o status do veículo é atualizado automaticamente.
- Dados de exemplo inseridos automaticamente na primeira execução.

## 🛠 Stack

| Componente | Versão |
| --- | --- |
| Gradle | 9.5.0 |
| Android Gradle Plugin (AGP) | 9.3.3 (built-in Kotlin) |
| Kotlin | 2.4.20 |
| Compose Compiler (plugin) | 2.4.20 |
| KSP | 2.3.12 |
| Compose BOM | 2026.09.00 (Material 3) |
| Room | 2.8.5 |
| Navigation Compose | 2.10.2 |
| compileSdk / targetSdk / minSdk | 37 / 36 / 24 |

## 📂 Estrutura do projeto


```
app/src/main/java/com/example/aluguelcarros/
├── AluguelCarrosApplication.kt      # Inicializa o banco e o repositório
├── MainActivity.kt                  # Ponto de entrada (Compose)
├── data/
│   ├── AppDatabase.kt               # Room database + seed de dados
│   ├── dao/                         # DAOs (Vehicle, Customer, Rental)
│   ├── model/                       # Entidades e enums
│   └── repository/RentalRepository.kt
└── ui/
    ├── navigation/                  # NavHost + bottom navigation
    ├── screens/                     # Dashboard, Veículos, Clientes, Locações
    └── theme/                       # Cores e tema Material 3
```
