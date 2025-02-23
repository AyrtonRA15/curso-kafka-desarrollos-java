package com.imagina.kafka.broker.stream.feedback;

import com.imagina.kafka.broker.message.FeedbackMessage;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

//@Component
public class FeedbackFourStream {

    private static final Set<String> GOOD_WORDS = java.util.Set.of("contento", "bueno", "amable");
    private static final Set<String> BAD_WORDS = Set.of("enfadado", "descontento", "malo");

    //@Autowired
    void kstreamFeedback(StreamsBuilder builder) {
        var stringSerde = Serdes.String();
        var feedbackSerde = new JsonSerde<>(FeedbackMessage.class);

        builder.stream("t-commodity-feedback", Consumed.with(stringSerde, feedbackSerde))
                .flatMap(splitWords())
                .split()
                .branch(isGoodWord(), Branched.withConsumer(
                        ks -> {
                            ks.to("t-commodity-feedback-four-good");
                            ks.groupByKey().count().toStream().to("t-commodity-feedback-four-good-count");
                        }))
                .branch(isBadWord(), Branched.withConsumer(
                        ks -> {
                            ks.to("t-commodity-feedback-four-bad");
                            ks.groupByKey().count().toStream().to("t-commodity-feedback-four-bad-count");
                        }));

    }

    private Predicate<String, String> isBadWord() {
        return (key, value) -> BAD_WORDS.contains(value);
    }

    private Predicate<String, String> isGoodWord() {
        return (key, value) -> GOOD_WORDS.contains(value);
    }

    private KeyValueMapper<String, FeedbackMessage, Iterable<KeyValue<String, String>>> splitWords() {
        return (key, value) -> Arrays
                .asList(value.getFeedback().replaceAll("[^a-zA-Z]", " ")
                        .toLowerCase().split("\\s+"))
                .stream()
                .distinct()
                .map(word -> KeyValue.pair(value.getLocation(), word)).collect(Collectors.toList());
    }

}
