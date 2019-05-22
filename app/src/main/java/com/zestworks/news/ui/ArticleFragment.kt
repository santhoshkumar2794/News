package com.zestworks.news.ui


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.zestworks.news.R
import com.zestworks.news.viewmodel.ModelUtils
import com.zestworks.news.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*


class ArticleFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(articleToolbar,findNavController())
        bindObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newsViewModel = ModelUtils.getNewsViewModel(activity = activity as AppCompatActivity)
    }

    private fun bindObservers() {
        newsViewModel.articleForId(ArticleFragmentArgs.fromBundle(arguments!!).articleId)
                .observe(
                        viewLifecycleOwner,
                        Observer {
                            webView.loadUrl(it.url)
                        }
                )
    }

}
