# [work.sayonara.voyage](https://work.sayonara.voyage)

小説公開サイトジェネレータ for 藤谷光

## 仕組み

小説それぞれにジャンル -> 作品の親子関係を持つことを前提にしています。

```
.
├── FanFiction
│   ├── Fanfiction_Genre1
│   │   ├── fanfic_g1_work1
│   │   │   ├── text.txt
│   │   │   └── work.json
│   │   └── genre.json
│   └── fanfic_genre2
│        ├── fanfic_g2_work1
│        │   ├── text.txt
│        │   └── work.json
│        └── genre.json
├── Original
│   ├── original_work
│   │   ├── text.txt
│   │   ├── work.json
│   ├── genre.json
└── README.md
```

- この場合、`Fanfiction_Genre1`, `Fanfiction_Genre2`, `Original` の3つのジャンルが存在し、それぞれ `fanfic_g1_work1` と `fanfic_g2_work1` と `original_work` の作品を持っていることになります。
- ジャンルはどの階層にあっても探索してくれるし、workもそれぞれのGenreディレクトリ以下であれば深くまで自動的に探索してくれます。
- 生成するときはフォルダ名を指定してあげてください。