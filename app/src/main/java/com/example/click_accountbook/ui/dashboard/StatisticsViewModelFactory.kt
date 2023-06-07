import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.click_accountbook.DatabaseHandler
import com.example.click_accountbook.ui.dashboard.StatisticsViewModel

class StatisticsViewModelFactory(private val dbHandler: DatabaseHandler) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(dbHandler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

