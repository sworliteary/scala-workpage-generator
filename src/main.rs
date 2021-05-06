use maud::{html, Markup, DOCTYPE};
use std::fs::{self, File};
use std::io::prelude::*;
use std::io::Result;
use std::path::{Path, PathBuf};
use std::rc::Rc;

trait Page {
    fn path(&self) -> String;
    fn page(&self) -> Markup;
}

enum GenreKind {
    Fanfiction,
    Original,
}

struct Genre {
    name: String,
    kind: GenreKind,
    path: String,
}

impl Page for Genre {
    fn path(&self) -> String {
        match &self.kind {
            GenreKind::Fanfiction => String::from("fanfiction/") + &self.path,
            GenreKind::Original => String::from("original"),
        }
    }
    fn page(&self) -> Markup {
        todo!()
    }
}

struct NovelInfo {
    tag: Vec<String>,
    title: String,
    path: String,
    caption: String,
}

struct Novel {
    text: String,
    info: NovelInfo,
    genre: Rc<Genre>,
}

impl Page for Novel {
    fn path(&self) -> String {
        self.genre.path() + "/" + &self.info.path
    }
    fn page(&self) -> Markup {
        html! {
            (DOCTYPE)
            html lang="ja" {
                head {
                    meta charset="utf-8";
                    meta name="viewport" content="width=device-width, initial-scale=1";
                }
            }
            body {
                h1 {(&self.info.title)}
                div.container {
                    p {(&self.text)}
                }
            }
        }
    }
}

fn generate_page(novel: Novel) -> Result<()> {
    let page = novel.page().into_string();
    let path;
    if !Path::new(&novel.path()).ends_with(".html") {
        path = String::from("dest/") + &novel.path() + "/index.html";
    } else {
        path = String::from("dest/") + &novel.path();
    }
    let parent = Path::new(&path).parent().unwrap();    if !parent.exists() {
        fs::create_dir_all(parent)?;
    }
    let mut file = File::create(path)?;
    file.write_all(page.as_bytes())?;
    Ok(())
}

fn main() {
    let path = "/Users/sh4869/Documents/Stamen";
    let paths: Vec<PathBuf>;
    match glob::glob(&format!("{}{}", path, "/**/work.toml")) {
        Ok(v) => paths = v.flat_map(|x| x).collect::<Vec<_>>(),
        Err(err) => panic!(),
    }
    for path in paths {
        let p = path.as_path();
        println!("{:?}", p.to_str())
    }
    let genre = Genre {
        kind: GenreKind::Fanfiction,
        name: String::from("テスト"),
        path: String::from("test"),
    };
    let info = NovelInfo {
        caption: String::from("説明"),
        tag: vec![String::from("aaa")],
        title: String::from("タイトル"),
        path: String::from("title"),
    };
    let novel = Novel {
        info: info,
        text: String::from("akaskdlfjaslidjfakldfj;kasdf"),
        genre: Rc::new(genre),
    };
    match generate_page(novel) {
        Ok(()) => (),
        Err(e) => (),
    }
    println!("Hello, world! {}", path);
}
