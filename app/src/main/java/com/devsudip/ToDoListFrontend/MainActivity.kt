package com.devsudip.ToDoListFrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.devsudip.ToDoListFrontend.ui.screens.TodoListScreen
import com.devsudip.ToDoListFrontend.ui.theme.ToDoListFrontendTheme
import com.devsudip.ToDoListFrontend.ui.viewmodel.TodoViewModel
import com.devsudip.ToDoListFrontend.ui.viewmodel.TodoViewModelFactory

class MainActivity : ComponentActivity() {


    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory((application as TodoApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListFrontendTheme {
                TodoListScreen(viewModel = todoViewModel)
            }
        }
    }
}

