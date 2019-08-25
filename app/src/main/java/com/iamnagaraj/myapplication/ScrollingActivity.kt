package com.iamnagaraj.myapplication
/*
*
* Author: Nagaraj Alagusundaram
* Email: naag.aus@gmail.com
*
 */
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iamnagaraj.myapplication.api.ApiInterface
import com.iamnagaraj.myapplication.binding.ImageAdapter
import com.iamnagaraj.myapplication.model.PexelDataModel
import com.iamnagaraj.myapplication.model.Photos
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.android.synthetic.main.display_no_result.*
import kotlinx.android.synthetic.main.display_progress_bar.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val PER_PAGE = 16
private const val PAGE_COUNT = 1
private const val TAG = "ScrollingActivity"
private const val DEFAULT_SEARCH_KEYWORD = "nature"
private const val DEFAULT_COLUMN_COUNT = 2
private const val URL_PEXEL = "https://www.pexels.com/"
class ScrollingActivity : AppCompatActivity() {
    private var nCompositeDisposable: CompositeDisposable? = null
    private var nPerPage = PER_PAGE
    private var nPageCount = PAGE_COUNT
    private var nPhotosArrayList: Array<Photos>? = null
    private var nImageAdapter: ImageAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        nCompositeDisposable = CompositeDisposable()
        initRecyclerView() //Initialize RecyclerView
        fetchJSON(DEFAULT_SEARCH_KEYWORD) //By default pass "nature" as string query
        fab.setOnClickListener {
            toggleSearch() // display edittext on fab clicked so that user can search
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nCompositeDisposable?.clear() //dispose
    }

    private fun initRecyclerView() {
        px_image_list.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        px_image_list.layoutManager = layoutManager
    }

    private fun fetchJSON(searchQuery: String) {
        nCompositeDisposable?.add(
            Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiInterface::class.java).fetchApiData(
                    searchQuery = searchQuery,
                    pageCount = nPageCount,
                    perPage = nPerPage
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::responseReceived, this::errorDetected)
        )
    }

    private fun responseReceived(pexelResponse: PexelDataModel) {
        Log.d(TAG, pexelResponse.total_results)
        nPhotosArrayList = pexelResponse.photos
        nImageAdapter = ImageAdapter(nPhotosArrayList!!, this)
        if (nImageAdapter?.itemCount!! > 0) {
            //At least 1 photo present
            px_image_list.adapter = nImageAdapter
            px_image_list.layoutManager = GridLayoutManager(this, DEFAULT_COLUMN_COUNT)
            displayResult()
        } else {
            //No Data available
            displayNoResult()
        }
    }

    private fun errorDetected(error: Throwable) {
        //Display error
        Log.d(TAG, error.localizedMessage)
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
        displayNoResult()

    }

    fun onItemClicked(photos: Photos) {
        Toast.makeText(this, "${photos.id} Clicked", Toast.LENGTH_SHORT).show()
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(photos.src?.original)))
    }

    fun onItemUploaderCliked(photos: Photos){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(photos.photographer_url)))
    }
    fun onItemPexelClicked(){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(URL_PEXEL)))
    }

    private fun displayResult() {
        no_result_layout.visibility = View.INVISIBLE
        display_progress_bar.visibility = View.INVISIBLE
        px_image_list.visibility = View.VISIBLE
    }

    private fun displayNoResult() {
        no_result_layout.visibility = View.VISIBLE
        display_progress_bar.visibility = View.INVISIBLE
        px_image_list.visibility = View.INVISIBLE

    }

    private fun viewVisibility(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.INVISIBLE
        } else {
            view.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        search_query.setOnEditorActionListener { editText, action, event ->
            var temp = false
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                toggleSearch()
                displayProgress()
                fetchJSON(editText.text.toString())
                editText.text = ""
                temp = true
                (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                    InputMethodManager.HIDE_IMPLICIT_ONLY,
                    0
                )
            }
            temp

        }
    }

    private fun toggleSearch() {
        viewVisibility(fab)
        viewVisibility(search_query)
    }

    private fun displayProgress() {
        no_result_layout.visibility = View.INVISIBLE
        display_progress_bar.visibility = View.VISIBLE
        px_image_list.visibility = View.INVISIBLE
    }
}
