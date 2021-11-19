package com.thana.simplegame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.thana.simplegame.databinding.FragmentLevelTenBinding
import com.thana.simplegame.ui.SharedViewModel
import com.thana.simplegame.ui.common.BaseFragment
import com.thana.simplegame.ui.common.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LevelTenFragment : BaseFragment(R.layout.fragment_level_ten), View.OnTouchListener,
    View.OnDragListener {

    private val binding by viewBinding(FragmentLevelTenBinding::bind)
    private val viewModel: SharedViewModel by viewModels()

    private var isExpanded = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        showHint()
        binding.next.setOnClickListener {
            nextLevel()
        }

    }

    private fun nextLevel() {
//        val action = LevelFiveFragmentDirections.actionLevelFiveFragmentToLevelSixFragment()
//        findNavController().navigate(action)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {

        binding.yellow.setOnTouchListener(this)
        binding.area.setOnDragListener(this)

    }

    private fun checkIfMixed(dragEvent: DragEvent, view: View) {

        val whitePandaXStart = binding.whitePanda.x
        val whitePandaYStart = binding.whitePanda.y

        val whitePandaXEnd = whitePandaXStart + binding.whitePanda.width
        val whitePandaEnd = whitePandaYStart + binding.whitePanda.height

        val yellowXStart = binding.yellow.x
        val yellowYStart = binding.yellow.y

        val yellowXEnd = yellowXStart + binding.yellow.width
        val yellowYEnd = yellowYStart + binding.yellow.height


        if (view.id == binding.yellow.id || view.id == binding.whitePanda.id) {
            if (
                dragEvent.x in whitePandaXStart..whitePandaXEnd
                && dragEvent.y in whitePandaYStart..whitePandaEnd
                && dragEvent.x in yellowXStart..yellowXEnd
                && dragEvent.y in yellowYStart..yellowYEnd
            ) {

                correctAnswer()

            }
        }
    }

    private fun showHint() {

        binding.hintRoot.setOnClickListener {
            if (isExpanded) expand() else collapse()
        }
        binding.expand.setOnClickListener {
            if (isExpanded) expand() else collapse()
        }
        binding.collapse.setOnClickListener {
            if (isExpanded) expand() else collapse()
        }
    }

    private fun expand() {

        binding.hint.visibility = View.VISIBLE
        binding.collapse.visibility = View.VISIBLE
        binding.expand.visibility = View.INVISIBLE
        isExpanded = false
    }

    private fun collapse() {
        binding.hint.visibility = View.GONE
        binding.collapse.visibility = View.INVISIBLE
        binding.expand.visibility = View.VISIBLE
        isExpanded = true

    }

    private fun correctAnswer() {

        val winAudio = ExoPlayer.Builder(requireContext()).build()

        val winUri = RawResourceDataSource.buildRawResourceUri(R.raw.win)

        winAudio.apply {
            setMediaItem(MediaItem.fromUri(winUri))
            prepare()
        }

        binding.yellowPanda.visibility = View.VISIBLE
        binding.yellow.visibility = View.INVISIBLE
        binding.whitePanda.visibility = View.INVISIBLE
        binding.right.visibility = View.VISIBLE
        binding.next.visibility = View.VISIBLE
        binding.celebrate.visibility = View.VISIBLE
        binding.celebrate.playAnimation()
        winAudio.play()

        if (viewModel.getScore() < 10) {
            viewModel.addScore()
        }

    }

    override fun onDrag(layoutview: View, dragevent: DragEvent): Boolean {

        val view = dragevent.localState as View

        when (dragevent.action) {

            DragEvent.ACTION_DRAG_ENTERED -> {
                view.alpha = 0.3f
                view.visibility = View.INVISIBLE

            }
            DragEvent.ACTION_DROP -> {
                view.alpha = 1.0f
                val owner = binding.area
                owner.removeView(view)

                val container = layoutview as ConstraintLayout

                view.x = dragevent.x - (view.width / 2)
                view.y = dragevent.y - (view.height / 2)
                container.addView(view)
                view.visibility = View.VISIBLE
                checkIfMixed(dragevent, view)

            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.alpha = 1.0f
                view.visibility = View.VISIBLE

            }

        }
        return true
    }


    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        return if (motionEvent.action == MotionEvent.ACTION_DOWN) {

            view.performClick()
            val shadowBuilder = View.DragShadowBuilder(view)

            view.startDragAndDrop(null, shadowBuilder, view, 0)

            true
        } else {
            false
        }
    }
}