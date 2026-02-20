package com.gusbarros.blockcalls.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gusbarros.blockcalls.presentation.theme.StatusActive
import com.gusbarros.blockcalls.presentation.theme.StatusInactive

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Atualizar status quando a tela é aberta
    LaunchedEffect(Unit) {
        viewModel.checkStatus()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Block Unknown Calls",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card de Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isActive) StatusActive else StatusInactive
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.isActive) "✓" else "✗",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (state.isActive) "Proteção Ativa" else "Proteção Inativa",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (state.isActive) {
                            "Chamadas de números desconhecidos estão sendo bloqueadas"
                        } else {
                            "Configure as permissões nas configurações do Android"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Card de Contador
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calls Blocked",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.blockedCount.toString(),
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }

            // Card de Informações
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Como Funciona",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "• Apenas números salvos nos seus contatos podem ligar para você\n\n" +
                                "• Chamadas de números desconhecidos são bloqueadas automaticamente\n\n" +
                                "• Você continua vendo as chamadas bloqueadas no registro",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.checkStatus() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Atualizar Status")
            }
        }
    }
}
