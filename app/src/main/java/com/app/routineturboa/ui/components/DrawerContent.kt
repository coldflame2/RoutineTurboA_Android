
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.routineturboa.R

@Composable
fun DrawerContent(onItemClicked: () -> Unit) {
    val context = LocalContext.current // Assuming you're calling this from a composable
    val appName = context.getString(R.string.app_name)

    Column(
        modifier = Modifier
            .padding(0.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 1.dp) // Adjust padding as needed
        ) {

            Image(
                painter = painterResource(id = R.drawable.routineturbo),
                contentDescription = "App Icon",
                modifier = Modifier.size(50.dp).clip(CircleShape)

            )

            Text(
                text = appName,
                style = MaterialTheme.typography.titleLarge // Use a larger heading style for title
            )
        }

        Text(
            text = "Sign in",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { startSigningIn(context, "Ok then well") }
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Sync",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClicked() }
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Settings",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClicked() }
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign Out",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClicked() }
                .padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun startSigningIn(context: Context, message: String) {
    Toast.makeText(context, "Test: $message", Toast.LENGTH_LONG).show()
}
