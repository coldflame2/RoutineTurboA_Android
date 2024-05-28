package com.app.routineturbo_android


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DatabaseHelper to ensure database is copied and set up
        DatabaseHelper(this).readableDatabase

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column {
                        Greeting("Welcome to Routine Turbo!")
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(message: String) {
    Text(text = message, style = MaterialTheme.typography.bodyLarge)
}

package com.app.routineturbo_android

@Composable
fun MainScreen(taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(RoutineRepository(LocalContext.current)))) {
    val tasks by taskViewModel.tasks.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFBBDEFB),
                            Color(0xFFE3F2FD),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(task)
                }
            }
        }
    }
}



package com.app.routineturbo_android

fun copyDatabase(context: Context) {
    val dbPath = context.getDatabasePath(DatabaseHelper.DATABASE_NAME).absolutePath
    val dbFile = File(dbPath)
    if (!dbFile.exists()) {
        context.assets.open(DatabaseHelper.DATABASE_NAME).use { inputStream ->
            FileOutputStream(dbPath).use { outputStream ->
                copyStream(inputStream, outputStream)
            }
        }
    }
}

@Throws(IOException::class)
private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }
}

package com.app.routineturbo_android

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RoutineTurbo.db"
    }

    object DailyRoutine : BaseColumns {
        const val TABLE_NAME = "daily_routine"
        const val COLUMN_NAME_START_TIME = "start_time"
        const val COLUMN_NAME_END_TIME = "end_time"
        const val COLUMN_NAME_DURATION = "duration"
        const val COLUMN_NAME_TASK_NAME = "task_name"
        const val COLUMN_NAME_REMINDERS = "reminders"
        const val COLUMN_NAME_TYPE = "type"
        const val COLUMN_NAME_POSITION = "position"
    }

    private val SQL_CREATE_ENTRIES =
        """CREATE TABLE IF NOT EXISTS ${DailyRoutine.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${DailyRoutine.COLUMN_NAME_START_TIME} DATETIME,
            ${DailyRoutine.COLUMN_NAME_END_TIME} DATETIME,
            ${DailyRoutine.COLUMN_NAME_DURATION} INTEGER,
            ${DailyRoutine.COLUMN_NAME_TASK_NAME} TEXT,
            ${DailyRoutine.COLUMN_NAME_REMINDERS} DATETIME,
            ${DailyRoutine.COLUMN_NAME_TYPE} TEXT,
            ${DailyRoutine.COLUMN_NAME_POSITION} INTEGER)"""

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${DailyRoutine.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabase()
        return super.getReadableDatabase()
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabase()
        return super.getWritableDatabase()
    }

    private fun copyDatabase() {
        val dbPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
        val dbFile = File(dbPath)
        if (!dbFile.exists()) {
            context.assets.open(DATABASE_NAME).use { inputStream: InputStream ->
                FileOutputStream(dbPath).use { outputStream: OutputStream ->
                    copyStream(inputStream, outputStream)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
    }
}

package com.app.routineturbo_android

class RoutineRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.readableDatabase

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val cursor: Cursor = db.query(
            DatabaseHelper.DailyRoutine.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION} ASC"
        )
        with(cursor) {
            while (moveToNext()) {
                val task = Task(
                    getInt(getColumnIndexOrThrow(BaseColumns._ID)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_START_TIME)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_END_TIME)),
                    getInt(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_DURATION)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_TASK_NAME)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_REMINDERS)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_TYPE)),
                    getInt(getColumnIndexOrThrow(DatabaseHelper.DailyRoutine.COLUMN_NAME_POSITION))
                )
                tasks.add(task)
            }
        }
        cursor.close()
        return tasks
    }
}

package com.app.routineturbo_android

data class Task(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val duration: Int,
    val taskName: String,
    val reminders: String,
    val type: String,
    val position: Int
)

package com.app.routineturbo_android

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            this.alpha = 0.99f // Trigger anti-aliasing
                        },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.Task,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${convertTo12HourFormat(task.startTime.split(" ")[1])} - ${convertTo12HourFormat(task.endTime.split(" ")[1])}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.graphicsLayer {
                        this.alpha = 0.99f // Trigger anti-aliasing
                    }
                )
                Text(
                    text = "${task.duration} minutes",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.graphicsLayer {
                        this.alpha = 0.99f // Trigger anti-aliasing
                    }
                )
            }
        }
    }
}

package com.app.routineturbo_android

class TaskViewModel(private val repository: RoutineRepository) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskList = repository.getAllTasks()
            _tasks.value = taskList
        }
    }
}

class TaskViewModelFactory(private val repository: RoutineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.app.routineturbo_android

fun convertTo12HourFormat(time: String): String {
    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = inputFormat.parse(time)
    return outputFormat.format(date!!)
}
