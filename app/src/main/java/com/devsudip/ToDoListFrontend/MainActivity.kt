package com.devsudip.ToDoListFrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.devsudip.ToDoListFrontend.ui.screens.TodoListScreen
import com.devsudip.ToDoListFrontend.ui.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TodoViewModel by viewModels {
        // Correctly referencing the inner factory class
        TodoViewModel.TodoViewModelFactory(
            (application as TodoApplication).repository,
            (application as TodoApplication).themeManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListScreen(viewModel = viewModel)
        }
    }
}

