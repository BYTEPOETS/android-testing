import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAssertion
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


fun withRecyclerView(recyclerViewId: Int) = RecyclerViewMatcher(recyclerViewId)

class RecyclerViewMatcher(private val recyclerViewId: Int) {
    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            var childView: View? = null
            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (resources != null) {
                    idDescription = try {
                        resources!!.getResourceName(recyclerViewId)
                    } catch (var4: NotFoundException) {
                        String.format(
                            "%s (resource name not found)",
                            *arrayOf<Any>(Integer.valueOf(recyclerViewId))
                        )
                    }
                }
                description.appendText("with id: $idDescription")
            }

            public override fun matchesSafely(view: View): Boolean {
                resources = view.resources
                if (childView == null) {
                    childView =
                        (view.rootView.findViewById<View>(recyclerViewId) as? RecyclerView)
                            ?.findViewHolderForAdapterPosition(position)?.itemView
                }
                return when {
                    childView == null -> {
                        false
                    }
                    targetViewId == -1 -> {
                        view === childView
                    }
                    else -> {
                        val targetView = childView!!.findViewById<View>(targetViewId)
                        view === targetView
                    }
                }
            }
        }
    }
}

fun hasItemsCount(count: Int): ViewAssertion {
    return ViewAssertion { view, e ->
        if (view !is RecyclerView) {
            throw e
        }
        assertThat(view.adapter!!.itemCount).isEqualTo(count)
    }
}
