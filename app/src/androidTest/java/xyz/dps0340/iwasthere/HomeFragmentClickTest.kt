package xyz.dps0340.iwasthere

import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.w3c.dom.Text
import java.util.regex.Matcher
import java.util.regex.Pattern

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentClickTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun goFragment() {
        closeSoftKeyboard()
        onView(withId(R.id.navigation_home))
            .perform(click())
    }

    @Test
    fun clickLocationButtonAndCheckChanges() {
        val floatPatternString = "[+-]?([0-9]*[.])?[0-9]+"
        val locationPattern = Pattern.compile("""latitude: $floatPatternString \| longitude: $floatPatternString""")
        // Check that the text was changed.
        onView(withId(R.id.location_button))
            .perform(click())
        SystemClock.sleep(3000)
        var match: Matcher? = null
        onView(withId(R.id.text_home))
            .check { view, noViewFoundException ->
                val button = view as TextView
                match = locationPattern.matcher(button.text)
                Log.i("TEST", button.text as String)
            }
        match?.let {
            assert(it.matches())
        } ?: assert(false)
    }
}