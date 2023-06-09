package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            repostByMe = true
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            if (post.likedByMe) {
                like.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
            countLikes.text = formatNumber(post.likes)
            countReposts.text = formatNumber(post.reposts)

            root.setOnClickListener {
                Log.d("stuff", "stuff")
            }

            /* avatar.setOnClickListener {
            Log.d("stuff", "avatar")
        }
        menu.setOnClickListener{
            Log.d("stuff", "menu")
        }*/


            like.setOnClickListener {
                Log.d("stuff", "like")
                post.likedByMe = !post.likedByMe
                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
                if (post.likedByMe) post.likes++ else post.likes--
                countLikes.text = formatNumber(post.likes)
            }
            repost.setOnClickListener {
                Log.d("stuff", "repost")
                if (post.repostByMe)
                    ++post.reposts
                countReposts.text = formatNumber(post.reposts)


            }
        }
    }

    private fun formatNumber(number: Int): String {
        return when {
            number < 1000 -> number.toString()
            number < 10000 -> {
                val integer = number / 1000 // 1
                val fractional = (number % 1000) / 100
                if (fractional > 0) {
                    "$integer.$fractional" + "K"
                } else {
                    "$integer" + "K"
                }
            }

            number < 1000000 -> (number / 1000).toString() + "K"
            else -> {
                val integer = number / 1000000
                val fractional = (number % 1000000) / 100000
                if (fractional > 0) {
                    "$integer.$fractional" + "M"
                } else "$integer" + "M"
            }

        }
    }

}
