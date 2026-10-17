package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.PointerArea;

public class IntroScene extends PixelScene {

	private enum State { TYPE, WAIT, FADE, DONE }

	private static final float CHAR_DELAY = 0.04f;
	private static final float FADE_TIME  = 0.8f;
	private static final float AUTO_NEXT  = 2.5f;

	private static final String[] PAGES = {
		"당신은 어느 날 밤, 잠들려 했지만 이불을 덮는 순간 이세계로 들어온 것 같았어요.",
		"그곳은 던전을 탐험하고, 몬스터를 잡고, 성장하고, 파밍하며 생존하는 곳이었어요.\n\n한 가지 독특한 점은, 당신이 움직일 때마다 몬스터도 같이 움직이고 당신이 움직이지 않으면 몬스터도 움직이지 않는다는 것.\n\n당신은 이 턴제 방식을 이용해 모든 층을 깨야 합니다."
	};

	private int page = 0;
	private int shown = 0;
	private float timer = 0;
	private State state = State.TYPE;

	private RenderedTextBlock text;
	private StyledButton btnStart;
	private int w, h;

	@Override
	public void create() {
		super.create();
		w = Camera.main.width;
		h = Camera.main.height;

		add(new ColorBlock(w, h, 0xFF000000));

		add(new PointerArea(0, 0, w, h) {
			@Override
			protected void onClick(PointerEvent event) { onTap(); }
		});

		text = PixelScene.renderTextBlock(9);
		text.maxWidth(Math.min(w - 20, 180));
		add(text);

		btnStart = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "게임 시작하기") {
			@Override
			protected void onClick() { Game.switchScene(InterlevelScene.class); }
		};
		btnStart.setRect((w - 100) / 2f, h - 40, 100, 20);
		btnStart.visible = btnStart.active = false;
		add(btnStart);

		layoutPage();
		fadeIn();
	}

	private void layoutPage() {
		text.text(PAGES[page]);
		text.setPos((w - text.width()) / 2f, (h - text.height()) / 2f - 10);
		align(text);
		text.text("");
		text.alpha(1f);
		shown = 0;
		timer = 0;
		state = State.TYPE;
	}

	@Override
	public void update() {
		super.update();
		timer += Game.elapsed;
		switch (state) {
			case TYPE:
				int target = Math.min(PAGES[page].length(), (int) (timer / CHAR_DELAY));
				if (target != shown) { shown = target; text.text(PAGES[page].substring(0, shown)); }
				if (shown >= PAGES[page].length()) finishTyping();
				break;
			case WAIT:
				if (timer >= AUTO_NEXT) { timer = 0; state = State.FADE; }
				break;
			case FADE:
				text.alpha(1f - Math.min(1f, timer / FADE_TIME));
				if (timer >= FADE_TIME) { page++; layoutPage(); }
				break;
			default: break;
		}
	}

	private void onTap() {
		if (state == State.TYPE) finishTyping();
		else if (state == State.WAIT) { timer = 0; state = State.FADE; }
	}

	private void finishTyping() {
		shown = PAGES[page].length();
		text.text(PAGES[page]);
		timer = 0;
		if (page < PAGES.length - 1) state = State.WAIT;
		else { state = State.DONE; btnStart.visible = btnStart.active = true; }
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
	}
}
