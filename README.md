# One Screen Launcher

Pixel 7で「1画面・高密度・親指優先」を検証するための個人用Androidランチャーです。

## v0.1 design principles

- **One Screen** — ホームページを増やさない
- **Intent First** — 無意識に開きたくないアプリは親指ゾーンへ置かない
- **Thumb First** — 意図的に素早く起動したいアプリほど下へ
- **Stable Position** — ランチャー側で勝手に詰めたり並べ替えたりしない
- **One Tap** — 主要アプリはホームから直接起動
- **Dense, not Minimal** — 減らすことより、高密度でも迷わないことを優先

## v0.1 implemented

- AndroidのHOMEアプリとして選択可能
- インストール済みランチャーアプリを `LauncherApps` から取得
- 4列・24枠を正式採用
- 4列の固定レイアウトを保存
- アプリ削除時にセルを自動で詰めない
- 編集モード：セル選択 → 別セルで入れ替え
- 選択セルのアプリ置換 / 空きセル化
- 上スワイプ：指に追従して下から検索ドロワーを引き上げる。検索欄はドロワー上部、結果は4列グリッド、アプリ＋Web横断検索
- 下スワイプ：Accessibility Service経由で通知パネルを開く
- Google検索バー / Discover / ニュース面なし
- パッケージ追加・削除・更新を `LauncherApps.Callback` で反映
- アプリ長押し：アプリ情報 / アンインストール
- ホーム空白長押し：編集モード
- 検索ドロワー上端のドラッグハンドルを下へ引くと閉じる

## intentionally deferred

- Android設定検索辞書
- 連絡先検索
- 天気
- カレンダー予定
- テーマアイコン
- JSONエクスポート/インポート
- 戻る/ホーム遷移アニメーションの最適化

## Build baseline

- Android Gradle Plugin 9.4.0
- Gradle 9.6.0
- Kotlin 2.4.20
- Compose BOM 2026.06.00
- Activity Compose 1.13.0
- Core KTX 1.17.0
- Lifecycle Runtime 2.10.0
- compileSdk / targetSdk 36
- JDK 17

## First run

1. APKをインストールする
2. Android設定 > デフォルトのアプリ > ホームアプリ で **One Screen** を選ぶ
3. 下スワイプ通知を使う場合、設定 > ユーザー補助 から **One Screen: 通知パネル** を有効にする
4. ホームの空白部分を長押し、または `⋮` → `ホームを編集` で配置を調整する

## v0.1 acceptance test on Pixel 7

- HOMEとして選べる
- 再起動後も配置と列数が維持される
- 4列・24枠でスクロールせず1画面に収まる
- アプリをアンインストールしても他セルが左詰めされない
- 上スワイプ直後に検索入力できる
- アプリ名完全一致ならアプリ起動、それ以外は明示的なWeb検索としてブラウザへ渡る
- 下スワイプで通知が開く（サービス有効時）
- Google/Discoverへの無意識導線がホーム上に存在しない
