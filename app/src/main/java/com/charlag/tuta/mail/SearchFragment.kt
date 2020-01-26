package com.charlag.tuta.mail

import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnNextLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.charlag.tuta.R
import com.charlag.tuta.util.withLifecycleContext
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.flow.debounce
import kotlin.math.hypot


data class Coordinates(val x: Int, val y: Int)

class SearchFragment
@JvmOverloads
constructor(val coordinates: Coordinates? = null) :
    Fragment(R.layout.fragment_search) {
    private val viewModel by activityViewModels<MailViewModel>()
    private val adapter = MailsAdapter {
        // TODO
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imm = view.context.getSystemService(InputMethodManager::class.java)

        coordinates?.let { (cx, cy) ->
            view.doOnNextLayout { view ->
                val radius = hypot(view.right.toDouble(), view.bottom.toDouble())
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0F, radius.toFloat()).apply {
                    interpolator = DecelerateInterpolator(2f)
                    duration = 600
                    doOnEnd {
                        subscribeToViewModel()
                        searchField.requestFocus()
                        imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
                    }
                    start()
                }
            }
        }

        recycler.layoutManager = LinearLayoutManager(view.context)
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            parentFragmentManager.popBackStack()
        }
    }

    private fun subscribeToViewModel() {
        val query = MutableLiveData<String>("")

        withLifecycleContext {
            query.asFlow()
                .debounce(200)
                .asLiveData()
                .switchMap(viewModel::search).observe {
                    adapter.submitList(it)
                }
        }

        searchField.doOnTextChanged { text, _, _, _ ->
            query.postValue(text.toString())
        }
    }
}