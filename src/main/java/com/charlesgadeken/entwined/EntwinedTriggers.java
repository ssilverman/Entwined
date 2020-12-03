package com.charlesgadeken.entwined;

import com.charlesgadeken.entwined.bpm.BPMTool;
import com.charlesgadeken.entwined.config.ConfigLoader;
import com.charlesgadeken.entwined.effects.EntwinedTriggerablePattern;
import com.charlesgadeken.entwined.effects.TSEffectController;
import com.charlesgadeken.entwined.effects.original.ColorEffect;
import com.charlesgadeken.entwined.effects.original.ScrambleEffect;
import com.charlesgadeken.entwined.effects.original.SpeedEffect;
import com.charlesgadeken.entwined.effects.original.SpinEffect;
import com.charlesgadeken.entwined.model.Model;
import com.charlesgadeken.entwined.patterns.EntwinedBasePattern;
import com.charlesgadeken.entwined.patterns.contributors.colinHunt.BeachBall;
import com.charlesgadeken.entwined.patterns.contributors.colinHunt.Breath;
import com.charlesgadeken.entwined.patterns.contributors.colinHunt.ColorWave;
import com.charlesgadeken.entwined.patterns.contributors.geoffSchmidt.Parallax;
import com.charlesgadeken.entwined.patterns.contributors.geoffSchmidt.Pixels;
import com.charlesgadeken.entwined.patterns.contributors.grantPatterson.Planes;
import com.charlesgadeken.entwined.patterns.contributors.grantPatterson.Pond;
import com.charlesgadeken.entwined.patterns.contributors.ireneZhou.*;
import com.charlesgadeken.entwined.patterns.contributors.jackLampack.AcidTrip;
import com.charlesgadeken.entwined.patterns.contributors.kyleFleming.*;
import com.charlesgadeken.entwined.patterns.contributors.markLottor.MarkLottor;
import com.charlesgadeken.entwined.patterns.contributors.maryWang.Twinkle;
import com.charlesgadeken.entwined.patterns.contributors.maryWang.VerticalSweep;
import com.charlesgadeken.entwined.patterns.contributors.raySykes.*;
import com.charlesgadeken.entwined.patterns.original.ColoredLeaves;
import com.charlesgadeken.entwined.patterns.original.SeeSaw;
import com.charlesgadeken.entwined.patterns.original.SweepPattern;
import com.charlesgadeken.entwined.patterns.original.Twister;
import com.charlesgadeken.entwined.triggers.ParameterTriggerableAdapter;
import com.charlesgadeken.entwined.triggers.Triggerable;
import com.charlesgadeken.entwined.triggers.drumpad.MidiEngine;
import com.charlesgadeken.entwined.triggers.drumpad.TSDrumpad;
import com.charlesgadeken.entwined.triggers.http.AppServer;
import heronarts.lx.LX;
import heronarts.lx.blend.LXBlend;
import heronarts.lx.effect.BlurEffect;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.mixer.LXChannel;
import heronarts.lx.parameter.*;
import heronarts.lx.pattern.LXPattern;
import java.util.ArrayList;
import java.util.List;

public class EntwinedTriggers {
    private final LX lx;

    BPMTool bpmTool;

    private TSDrumpad apc40Drumpad;
    ArrayList<Triggerable>[] apc40DrumpadTriggerablesLists;
    Triggerable[][] apc40DrumpadTriggerables;
    MidiEngine midiEngine;
    LXListenableNormalizedParameter[] effectKnobParameters;
    public final EngineController engineController;

    private final EntwinedParameters parameters;
    private final Model model;

    public EntwinedTriggers(
            LX lx, Model model, EngineController engineController, EntwinedParameters parameters) {
        this.lx = lx;
        this.model = model;
        this.engineController = engineController;
        this.parameters = parameters;
    }

    @SuppressWarnings("unchecked")
    void configureTriggerables() {
        if (apc40Drumpad != null) {
            apc40DrumpadTriggerablesLists =
                    new ArrayList[] {
                        new ArrayList<Triggerable>(),
                        new ArrayList<Triggerable>(),
                        new ArrayList<Triggerable>(),
                        new ArrayList<Triggerable>(),
                        new ArrayList<Triggerable>(),
                        new ArrayList<Triggerable>()
                    };
        }

        registerPatternTriggerables();
        registerOneShotTriggerables();
        registerEffectTriggerables();

        if (ConfigLoader.enableIPad) {
            // NOTE(meawoppl) slightly hacky way to get offsets/indices into a parent array
            engineController.startEffectIndex = lx.engine.mixer.masterBus.getEffects().size();
            registerIPadEffects();
            engineController.endEffectIndex = lx.engine.mixer.masterBus.getEffects().size();
        }

        if (apc40Drumpad != null) {
            apc40DrumpadTriggerables = new Triggerable[apc40DrumpadTriggerablesLists.length][];
            for (int i = 0; i < apc40DrumpadTriggerablesLists.length; i++) {
                ArrayList<Triggerable> triggerablesList = apc40DrumpadTriggerablesLists[i];
                apc40DrumpadTriggerables[i] = triggerablesList.toArray(new Triggerable[0]);
            }
            apc40DrumpadTriggerablesLists = null;
        }
    }

    void registerIPadEffects() {
        ColorEffect colorEffect = new ColorEffect(lx);
        ColorStrobeTextureEffect colorStrobeTextureEffect = new ColorStrobeTextureEffect(lx);
        FadeTextureEffect fadeTextureEffect = new FadeTextureEffect(lx);
        AcidTripTextureEffect acidTripTextureEffect = new AcidTripTextureEffect(lx);
        CandyTextureEffect candyTextureEffect = new CandyTextureEffect(lx);
        CandyCloudTextureEffect candyCloudTextureEffect = new CandyCloudTextureEffect(lx);
        // GhostEffect ghostEffect = new GhostEffect(lx);
        // RotationEffect rotationEffect = new RotationEffect(lx);

        SpeedEffect speedEffect = engineController.speedEffect;
        SpinEffect spinEffect = engineController.spinEffect;
        BlurEffect blurEffect = engineController.blurEffect;
        ScrambleEffect scrambleEffect = engineController.scrambleEffect;
        // StaticEffect staticEffect = engineController.staticEffect = new StaticEffect(lx);

        lx.addEffect(blurEffect);
        lx.addEffect(colorEffect);
        // lx.addEffect(staticEffect);
        lx.addEffect(spinEffect);
        lx.addEffect(speedEffect);
        lx.addEffect(colorStrobeTextureEffect);
        lx.addEffect(fadeTextureEffect);
        // lx.addEffect(acidTripTextureEffect);
        lx.addEffect(candyTextureEffect);
        lx.addEffect(candyCloudTextureEffect);
        // lx.addEffect(ghostEffect);
        lx.addEffect(scrambleEffect);
        // lx.addEffect(rotationEffect);

        registerEffectController(
                "Rainbow", candyCloudTextureEffect, candyCloudTextureEffect.amount);
        registerEffectController("Candy Chaos", candyTextureEffect, candyTextureEffect.amount);
        registerEffectController(
                "Color Strobe", colorStrobeTextureEffect, colorStrobeTextureEffect.amount);
        registerEffectController("Fade", fadeTextureEffect, fadeTextureEffect.amount);
        registerEffectController("Monochrome", colorEffect, colorEffect.mono);
        registerEffectController("White", colorEffect, colorEffect.desaturation);
    }

    void addPatterns(List<LXPattern> patterns) {
        // Add patterns here.
        // The order here is the order it shows up in the patterns list
        // patterns.add(new SolidColor(lx));
        // patterns.add(new ClusterLineTest(lx));
        // patterns.add(new OrderTest(lx));
        patterns.add(new Twister(lx));
        patterns.add(new CandyCloud(lx));
        patterns.add(new MarkLottor(lx));
        patterns.add(new SolidColor(lx));
        // patterns.add(new DoubleHelix(lx));
        patterns.add(new SparkleHelix(lx));
        patterns.add(new Lightning(lx));
        patterns.add(new SparkleTakeOver(lx));
        patterns.add(new MultiSine(lx));
        patterns.add(new Ripple(lx));
        patterns.add(new SeeSaw(lx));
        patterns.add(new SweepPattern(lx));
        patterns.add(new IceCrystals(lx));
        patterns.add(new ColoredLeaves(lx));
        patterns.add(new Stripes(lx));
        patterns.add(new AcidTrip(lx));
        patterns.add(new Springs(lx));
        patterns.add(new Lattice(lx));
        patterns.add(new Fire(lx));
        patterns.add(new Fireflies(lx));
        patterns.add(new Fumes(lx));
        patterns.add(new Voronoi(lx));
        patterns.add(new Cells(lx));
        patterns.add(new Bubbles(lx));
        patterns.add(new Pulleys(lx));

        patterns.add(new Wisps(lx));
        patterns.add(new Explosions(lx));
        patterns.add(new BassSlam(lx));
        patterns.add(new Rain(lx));
        patterns.add(new Fade(lx));
        patterns.add(new Strobe(lx));
        patterns.add(new Twinkle(lx));
        patterns.add(new VerticalSweep(lx));
        patterns.add(new RandomColor(lx));
        patterns.add(new ColorStrobe(lx));
        patterns.add(new Pixels(lx));
        // patterns.add(new Wedges(lx));
        patterns.add(new Parallax(lx));

        // Colin Hunt Patterns
        patterns.add(new ColorWave(lx));
        patterns.add(new BeachBall(lx));
        patterns.add(new Breath(lx));

        // Grant Patterson Patterns
        patterns.add(new Pond(lx));
        patterns.add(new Planes(lx));
    }

    void configureMIDI() {
        apc40Drumpad = new TSDrumpad();
        apc40Drumpad.triggerables = apc40DrumpadTriggerables;

        bpmTool = new BPMTool(lx, effectKnobParameters);

        // MIDI control
        midiEngine = new MidiEngine(lx, parameters, apc40Drumpad, bpmTool, null);
    }

    void registerPatternTriggerables() {
        // The 2nd parameter is the NFC tag serial number
        // Specify a blank string to only add it to the apc40 drumpad
        // The 3rd parameter is which row of the apc40 drumpad to add it to.
        // defaults to the 3rd row
        // the row parameter is zero indexed

        registerPattern(new Twister(lx));
        registerPattern(new MarkLottor(lx));
        registerPattern(new Ripple(lx));
        registerPattern(new Stripes(lx));
        registerPattern(new Lattice(lx));
        registerPattern(new Fumes(lx));
        registerPattern(new Voronoi(lx));
        registerPattern(new CandyCloud(lx));
        registerPattern(new GalaxyCloud(lx));

        registerPattern(new ColorStrobe(lx), 3);
        registerPattern(new Explosions(lx, 20), 3);
        registerPattern(new Strobe(lx), 3);
        registerPattern(new SparkleTakeOver(lx), 3);
        registerPattern(new MultiSine(lx), 3);
        registerPattern(new SeeSaw(lx), 3);
        registerPattern(new Cells(lx), 3);
        registerPattern(new Fade(lx), 3);
        registerPattern(new Pixels(lx), 3);

        registerPattern(new IceCrystals(lx), 5);
        registerPattern(new Fire(lx), 5); // Make red

        // registerPattern(new DoubleHelix(lx), "");
        registerPattern(new AcidTrip(lx));
        registerPattern(new Rain(lx));

        registerPattern(new Wisps(lx, 1, 60, 50, 270, 20, 3.5, 10)); // downward yellow wisp
        registerPattern(new Wisps(lx, 30, 210, 100, 90, 20, 3.5, 10)); // colorful wisp storm
        registerPattern(
                new Wisps(lx, 1, 210, 100, 90, 130, 3.5, 10)); // multidirection colorful wisps
        registerPattern(new Wisps(lx, 3, 210, 10, 270, 0, 3.5, 10)); // rain storm of wisps
        registerPattern(new Wisps(lx, 35, 210, 180, 180, 15, 2, 15)); // twister of wisps

        registerPattern(new Pond(lx));
        registerPattern(new Planes(lx));
    }

    void registerOneShotTriggerables() {
        registerOneShot(new Pulleys(lx));
        registerOneShot(new StrobeOneshot(lx));
        registerOneShot(new BassSlam(lx));
        registerOneShot(new Fireflies(lx, 70, 6, 180));
        registerOneShot(new Fireflies(lx, 40, 7.5f, 90));

        registerOneShot(new Fireflies(lx), 5);
        registerOneShot(new Bubbles(lx), 5);
        registerOneShot(new Lightning(lx), 5);
        registerOneShot(new Wisps(lx), 5);
        registerOneShot(new Explosions(lx), 5);
    }

    void registerOneShot(EntwinedTriggerablePattern pattern) {
        registerOneShot(pattern, 4);
    }

    void registerOneShot(EntwinedTriggerablePattern pattern, int apc40DrumpadRow) {
        registerVisual(pattern, apc40DrumpadRow);
    }

    void registerEffectTriggerables() {
        BlurEffect blurEffect = new TSBlurEffect(lx);
        ColorEffect colorEffect = new ColorEffect(lx);
        GhostEffect ghostEffect = new GhostEffect(lx);
        ScrambleEffect scrambleEffect = new ScrambleEffect(lx);
        StaticEffect staticEffect = new StaticEffect(lx);
        RotationEffect rotationEffect = new RotationEffect(lx);
        SpinEffect spinEffect = new SpinEffect(lx);
        SpeedEffect speedEffect = new SpeedEffect(lx);
        ColorStrobeTextureEffect colorStrobeTextureEffect = new ColorStrobeTextureEffect(lx);
        FadeTextureEffect fadeTextureEffect = new FadeTextureEffect(lx);
        AcidTripTextureEffect acidTripTextureEffect = new AcidTripTextureEffect(lx);
        CandyTextureEffect candyTextureEffect = new CandyTextureEffect(lx);
        CandyCloudTextureEffect candyCloudTextureEffect = new CandyCloudTextureEffect(lx);

        lx.addEffect(blurEffect);
        lx.addEffect(colorEffect);
        lx.addEffect(ghostEffect);
        lx.addEffect(scrambleEffect);
        lx.addEffect(staticEffect);
        lx.addEffect(rotationEffect);
        lx.addEffect(spinEffect);
        lx.addEffect(speedEffect);
        lx.addEffect(colorStrobeTextureEffect);
        lx.addEffect(fadeTextureEffect);
        lx.addEffect(acidTripTextureEffect);
        lx.addEffect(candyTextureEffect);
        lx.addEffect(candyCloudTextureEffect);

        registerEffectControlParameter(speedEffect.speed, 1, 0.4);
        registerEffectControlParameter(speedEffect.speed, 1, 5);
        registerEffectControlParameter(colorEffect.rainbow);
        registerEffectControlParameter(colorEffect.mono);
        registerEffectControlParameter(colorEffect.desaturation);
        registerEffectControlParameter(colorEffect.sharp);
        registerEffectControlParameter(blurEffect.level, 0.65);
        registerEffectControlParameter(spinEffect.spin, 0.65);
        registerEffectControlParameter(ghostEffect.amount, 0, 0.16, 1);
        registerEffectControlParameter(scrambleEffect.amount, 0, 1, 1);
        registerEffectControlParameter(colorStrobeTextureEffect.amount, 0, 1, 1);
        registerEffectControlParameter(fadeTextureEffect.amount, 0, 1, 1);
        registerEffectControlParameter(acidTripTextureEffect.amount, 0, 1, 1);
        registerEffectControlParameter(candyCloudTextureEffect.amount, 0, 1, 1);
        registerEffectControlParameter(staticEffect.amount, 0, .3, 1);
        registerEffectControlParameter(candyTextureEffect.amount, 0, 1, 5);

        parameters.effectKnobParameters =
                new LXListenableNormalizedParameter[] {
                    colorEffect.hueShift,
                    colorEffect.mono,
                    colorEffect.desaturation,
                    colorEffect.sharp,
                    blurEffect.level,
                    speedEffect.speed,
                    spinEffect.spin,
                    candyCloudTextureEffect.amount
                };
    }

    public void registerPattern(EntwinedTriggerablePattern pattern) {
        registerPattern(pattern, 2);
    }

    public void registerPattern(EntwinedTriggerablePattern pattern, int apc40DrumpadRow) {
        registerVisual(pattern, apc40DrumpadRow);
    }

    void registerVisual(EntwinedTriggerablePattern pattern, int apc40DrumpadRow) {

        // TODO(meawoppl)
        // NOTE(meawoppl) @Slee same question below.  re `.setDuration(dissolveTime);`
        // LXBlend t = new DissolveBlend(lx);
        // pattern.setTransition(t);

        Triggerable triggerable = configurePatternAsTriggerable(pattern);
        if (apc40Drumpad != null) {
            apc40DrumpadTriggerablesLists[apc40DrumpadRow].add(triggerable);
        }
    }

    Triggerable configurePatternAsTriggerable(EntwinedTriggerablePattern pattern) {
        LXChannel channel = lx.engine.mixer.addChannel(new EntwinedBasePattern[] {pattern});
        setupChannel(channel, false);

        pattern.onTriggerableModeEnabled();
        return pattern.getTriggerable();
    }

    void setupChannel(final LXChannel channel, boolean noOpWhenNotRunning) {
        // TODO(meawoppl)
        // @Slee, honestly no idea what the intention is here...
        // It looks like patterns used to have associated traditionss" have have moved to the
        // `blend` language, but I don't seen any analogue for that remaining....
        //        channel.setFaderTransition(
        //                new TreesTransition(lx, channel, model, parameters.channelTreeLevels,
        // parameters.channelShrubLevels));

        // Trying the following:
        channel.transitionBlendMode.setObjects(
                new TreesTransition[] {
                    new TreesTransition(
                            lx,
                            channel,
                            model,
                            parameters.channelTreeLevels,
                            parameters.channelShrubLevels)
                });

        channel.addListener(
                new LXChannel.Listener() {
                    LXBlend transition;

                    @Override
                    public void patternWillChange(
                            LXChannel channel, LXPattern pattern, LXPattern nextPattern) {
                        if (!channel.enabled.isOn()) {
                            // TODO(meawoppl) - IDK
                            // transition = nextPattern.getTransition();
                            //  nextPattern.setTransition(null);
                        }
                    }

                    @Override
                    public void patternDidChange(LXChannel channel, LXPattern pattern) {
                        if (transition != null) {
                            // TODO(meawoppl) IDK
                            // pattern.setTransition(transition);
                            // transition = null;
                        }
                    }
                });

        if (noOpWhenNotRunning) {
            channel.enabled.setValue(channel.fader.getValue() != 0);
            channel.fader.addListener(
                    (parameter) -> channel.enabled.setValue(channel.fader.getValue() != 0));
        }
    }

    void registerEffectControlParameter(LXListenableNormalizedParameter parameter) {
        registerEffectControlParameter(parameter, 0, 1, 0);
    }

    void registerEffectControlParameter(LXListenableNormalizedParameter parameter, double onValue) {
        registerEffectControlParameter(parameter, 0, onValue, 0);
    }

    void registerEffectControlParameter(
            LXListenableNormalizedParameter parameter, double offValue, double onValue) {
        registerEffectControlParameter(parameter, offValue, onValue, 0);
    }

    void registerEffectControlParameter(
            LXListenableNormalizedParameter parameter, double offValue, double onValue, int row) {
        ParameterTriggerableAdapter triggerable =
                new ParameterTriggerableAdapter(lx, parameter, offValue, onValue);
        if (apc40Drumpad != null) {
            apc40DrumpadTriggerablesLists[row].add(triggerable);
        }
    }

    void registerEffectController(
            String name, LXEffect effect, LXListenableNormalizedParameter parameter) {
        ParameterTriggerableAdapter triggerable = new ParameterTriggerableAdapter(lx, parameter);
        TSEffectController effectController = new TSEffectController(name, effect, triggerable);

        engineController.effectControllers.add(effectController);
    }

    void configureServer() {
        new AppServer(lx, engineController).start();
    }
}
