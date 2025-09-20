package com.devsudip.ToDoListFrontend.ui.screens

import android.app.Activity
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.devsudip.ToDoListFrontend.data.TodoItem
import com.devsudip.ToDoListFrontend.data.User
import com.devsudip.ToDoListFrontend.ui.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Light Mode
val LightBackground = Color(0xFFF8F9FA)
val LogoCyan = Color(0xFF00E0FF)
val LightPrimaryText = Color(0xFF212529)
val LightSecondaryText = Color(0xFF6C757D)
val CompletedGreen = Color(0xFF28A745)
val OverdueRed = Color(0xFFDC3545)

// Dark Mode
val DarkBackground = Color(0xFF0D1117)
val DarkSurface = Color(0xFF161B22)
val DarkPrimaryText = Color(0xFFC9D1D9)
val DarkSecondaryText = Color(0xFF8B949E)


private object UserStateInitial

@Composable
fun TodoListScreen(viewModel: TodoViewModel) {
    var isDarkMode by remember { mutableStateOf(false) }
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
        }
    }

    val userState by viewModel.user.observeAsState(initial = UserStateInitial)
    val allTasks by viewModel.allTodos.observeAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TodoItem?>(null) }
    var showOverdueDialog by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkMode) DarkBackground else LightBackground
    val accentColor = LogoCyan

    when (val user = userState) {
        UserStateInitial -> {
            Box(modifier = Modifier.fillMaxSize().background(backgroundColor), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
        }
        null -> {
            GetUserNameDialog(isDarkMode = isDarkMode) { name ->
                viewModel.saveUserName(name)
            }
        }
        is User -> {
            val (overdueTasks, upcomingTasks) = allTasks.partition { isTaskOverdue(it.dueDate) && !it.isCompleted }

            MainContent(
                user = user,
                upcomingTasks = upcomingTasks,
                overdueTaskCount = overdueTasks.size,
                isDarkMode = isDarkMode,
                onToggleTheme = { isDarkMode = !isDarkMode },
                onAddTaskClicked = { showAddDialog = true },
                onOverdueClicked = { showOverdueDialog = true },
                onTaskCheckedChange = { task, isCompleted ->
                    viewModel.updateTodo(task.copy(isCompleted = isCompleted))
                },
                onDeleteTaskClicked = { task ->
                    taskToDelete = task
                }
            )

            if (showOverdueDialog) {
                OverdueTasksDialog(
                    overdueTasks = overdueTasks,
                    isDarkMode = isDarkMode,
                    onDismiss = { showOverdueDialog = false },
                    onDeleteTask = { viewModel.deleteTodo(it) },
                    onTaskCheckedChange = { task, isCompleted ->
                        viewModel.updateTodo(task.copy(isCompleted = isCompleted))
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddTodoDialog(
            isDarkMode = isDarkMode,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, dueDate ->
                viewModel.addTodo(TodoItem(title = title, category = category, dueDate = dueDate))
                showAddDialog = false
            }
        )
    }

    taskToDelete?.let { task ->
        DeleteConfirmationDialog(
            isDarkMode = isDarkMode,
            taskTitle = task.title,
            onDismiss = { taskToDelete = null },
            onConfirm = {
                viewModel.deleteTodo(task)
                taskToDelete = null
            }
        )
    }
}

@Composable
fun MainContent(
    user: User,
    upcomingTasks: List<TodoItem>,
    overdueTaskCount: Int,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onAddTaskClicked: () -> Unit,
    onOverdueClicked: () -> Unit,
    onTaskCheckedChange: (TodoItem, Boolean) -> Unit,
    onDeleteTaskClicked: (TodoItem) -> Unit
) {
    val groupedTasks = upcomingTasks.groupBy {
        formatDueDateForGrouping(it.dueDate)
    }

    val backgroundColor = if (isDarkMode) DarkBackground else LightBackground
    val primaryTextColor = if (isDarkMode) DarkPrimaryText else LightPrimaryText
    val secondaryTextColor = if (isDarkMode) DarkSecondaryText else LightSecondaryText
    val iconColor = if (isDarkMode) DarkPrimaryText else LightPrimaryText

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClicked,
                containerColor = LogoCyan
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Task", tint = DarkBackground)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Hello ${user.name}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = primaryTextColor,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = iconColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Here are your tasks:", color = secondaryTextColor)
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = overdueTaskCount > 0) {
                Button(
                    onClick = onOverdueClicked,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OverdueRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("$overdueTaskCount Overdue Task${if(overdueTaskCount > 1) "s" else ""}", color = OverdueRed, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (upcomingTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(bottom = 80.dp), contentAlignment = Alignment.Center
                        ) {
                            Text("No upcoming tasks. Great job!", color = secondaryTextColor, fontSize = 18.sp)
                        }
                    }
                } else {
                    groupedTasks.forEach { (date, tasks) ->
                        item {
                            Text(
                                text = date,
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(tasks) { task ->
                            TaskItemCard(
                                task = task,
                                isDarkMode = isDarkMode,
                                onCheckedChange = { onTaskCheckedChange(task, it) },
                                onDeleteClicked = { onDeleteTaskClicked(task) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverdueTasksDialog(
    overdueTasks: List<TodoItem>,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onDeleteTask: (TodoItem) -> Unit,
    onTaskCheckedChange: (TodoItem, Boolean) -> Unit
) {
    val containerColor = if (isDarkMode) DarkSurface else LightBackground
    val titleColor = if (isDarkMode) DarkPrimaryText else LightPrimaryText

    AlertDialog(
        containerColor = containerColor,
        onDismissRequest = onDismiss,
        title = { Text("Overdue Tasks", color = titleColor) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(overdueTasks) { task ->
                    TaskItemCard(
                        task = task,
                        isDarkMode = isDarkMode,
                        onCheckedChange = { onTaskCheckedChange(task, it) },
                        onDeleteClicked = { onDeleteTask(task) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = LogoCyan)
            }
        }
    )
}


@Composable
fun TaskItemCard(
    task: TodoItem,
    isDarkMode: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClicked: () -> Unit
) {
    val isOverdue = !task.isCompleted && isTaskOverdue(task.dueDate)
    val defaultCardColor = if (isDarkMode) DarkSurface else Color.White
    val cardColor = if (isOverdue) OverdueRed.copy(alpha = 0.1f) else defaultCardColor
    val primaryTextColor = if (isDarkMode) DarkPrimaryText else LightPrimaryText
    val secondaryTextColor = if (isDarkMode) DarkSecondaryText else LightSecondaryText

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 1.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) CompletedGreen else LightSecondaryText)
                    .clickable { onCheckedChange(!task.isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverdue) OverdueRed else primaryTextColor
                )
                Text(task.category, color = secondaryTextColor, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))
            if (!isOverdue) {
                Text(formatDueDate(task.dueDate), color = secondaryTextColor, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onDeleteClicked) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = secondaryTextColor)
            }
        }
    }
}

@Composable
fun AddTodoDialog(
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    var selectedDueDate by remember { mutableStateOf(calendar.timeInMillis) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            selectedDueDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

    val containerColor = if (isDarkMode) DarkSurface else LightBackground
    val titleColor = if (isDarkMode) DarkPrimaryText else LightPrimaryText
    val secondaryTextColor = if (isDarkMode) DarkSecondaryText else LightSecondaryText

    AlertDialog(
        containerColor = containerColor,
        onDismissRequest = onDismiss,
        title = { Text("New Task", color = titleColor) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = LogoCyan,
                        unfocusedIndicatorColor = secondaryTextColor,
                        cursorColor = LogoCyan,
                        focusedLabelColor = LogoCyan,
                        unfocusedLabelColor = secondaryTextColor,
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("About Task") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = LogoCyan,
                        unfocusedIndicatorColor = secondaryTextColor,
                        cursorColor = LogoCyan,
                        focusedLabelColor = LogoCyan,
                        unfocusedLabelColor = secondaryTextColor,
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Due Date", fontWeight = FontWeight.Medium, color = titleColor)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val today = Calendar.getInstance()
                            selectedDueDate = today.timeInMillis
                        }, colors = ButtonDefaults.buttonColors(containerColor = LogoCyan)) {
                        Text("Today", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val tomorrow = Calendar.getInstance()
                            tomorrow.add(Calendar.DAY_OF_YEAR, 1)
                            selectedDueDate = tomorrow.timeInMillis
                        }, colors = ButtonDefaults.buttonColors(containerColor = LogoCyan)) {
                        Text("Tomorrow", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { datePickerDialog.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = secondaryTextColor)
                ) {
                    Text("Select a Date", color = if(isDarkMode) DarkBackground else LightBackground, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "Selected: ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(selectedDueDate))}",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 14.sp,
                    color = secondaryTextColor
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, category, selectedDueDate) }, colors = ButtonDefaults.buttonColors(containerColor = CompletedGreen)) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = secondaryTextColor)
            }
        }
    )
}


@Composable
fun GetUserNameDialog(isDarkMode: Boolean, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    val isConfirmEnabled by remember { derivedStateOf { name.isNotBlank() } }

    val containerColor = if (isDarkMode) DarkSurface else LightBackground
    val titleColor = if (isDarkMode) DarkPrimaryText else LightPrimaryText
    val textColor = if (isDarkMode) DarkSecondaryText else LightSecondaryText

    AlertDialog(
        containerColor = containerColor,
        onDismissRequest = { /* Prevent dismissing by clicking outside */ },
        title = { Text("Welcome!", color = titleColor) },
        text = {
            Column {
                Text("Please enter your name to get started.", color = textColor)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = LogoCyan,
                        unfocusedIndicatorColor = textColor,
                        cursorColor = LogoCyan,
                        focusedLabelColor = LogoCyan,
                        unfocusedLabelColor = textColor,
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = isConfirmEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = LogoCyan)
            ) {
                Text("Continue", color = DarkBackground)
            }
        },
        dismissButton = {}
    )
}


@Composable
fun DeleteConfirmationDialog(
    isDarkMode: Boolean,
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val containerColor = if (isDarkMode) DarkSurface else LightBackground
    val titleColor = if (isDarkMode) DarkPrimaryText else LightPrimaryText
    val textColor = if (isDarkMode) DarkSecondaryText else LightSecondaryText

    AlertDialog(
        containerColor = containerColor,
        onDismissRequest = onDismiss,
        title = { Text("Delete Task", color = titleColor) },
        text = { Text("Are you sure you want to delete the task \"$taskTitle\"?", color = textColor) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = OverdueRed)
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        }
    )
}

private fun formatDueDateForGrouping(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    val today = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

    return when {
        isSameDay(calendar, today) -> "Today"
        isSameDay(calendar, tomorrow) -> "Tomorrow"
        else -> SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatDueDate(dueDateMillis: Long): String {
    val dueDate = Calendar.getInstance().apply { timeInMillis = dueDateMillis }
    val today = Calendar.getInstance()

    return when {
        isSameDay(dueDate, today) -> "Today"
        else -> {
            val daysLeft = TimeUnit.MILLISECONDS.toDays(dueDateMillis - today.timeInMillis)
            if (daysLeft in 0..6) {
                SimpleDateFormat("EEE", Locale.getDefault()).format(Date(dueDateMillis))
            } else {
                SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(dueDateMillis))
            }
        }
    }
}


private fun isTaskOverdue(dueDateMillis: Long): Boolean {
    val dueDate = Calendar.getInstance().apply {
        timeInMillis = dueDateMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return dueDate.before(today)
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

