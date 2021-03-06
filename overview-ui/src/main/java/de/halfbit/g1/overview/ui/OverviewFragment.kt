package de.halfbit.g1.overview.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.halfbit.g1.base.MainUiFragmentFactory
import de.halfbit.g1.base.createSubscope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import magnet.Instance
import magnet.Scope
import magnet.bind
import magnet.getSingle

internal const val ROOT = "root"
internal const val MAIN_THREAD_SCHEDULER = "main-thread-scheduler"

class OverviewFragment : Fragment() {

    private lateinit var viewManager: ViewManager
    private var scope: Scope? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.overview_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        scope = activity.createSubscope {
            bind(checkNotNull(view) { "Fragment must create a view" }, ROOT)
            bind(AndroidSchedulers.mainThread(), MAIN_THREAD_SCHEDULER)
            bind(CompositeDisposable())
            viewManager = getSingle()
        }
    }

    override fun onStart() {
        super.onStart()
        viewManager.bind()
    }

    override fun onPause() {
        viewManager.unbind()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope = null
    }

}

@Instance(type = ViewManager::class)
internal class ViewManager(
    private val overviewView: OverviewView,
    private val overviewViewModel: OverviewViewModel,
    private val compositeDisposable: CompositeDisposable
) {

    fun bind() {
        overviewViewModel.bind(overviewView, compositeDisposable)
    }

    fun unbind() {
        compositeDisposable.clear()
    }

}

@Instance(type = MainUiFragmentFactory::class)
internal class OverviewFragmentFactory(
    private val context: Context
) : MainUiFragmentFactory {

    override fun create(): Fragment =
        Fragment.instantiate(context, OverviewFragment::class.java.name)

}
