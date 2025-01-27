package net.cactusthorn.config.core;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import net.cactusthorn.config.core.loader.Loaders;

public final class Config_TestConfig implements TestConfig {
  private final ConcurrentHashMap<String, Object> VALUES = new ConcurrentHashMap<>();

  public Config_TestConfig(final Loaders loaders) {
    ConfigInitializer_TestConfig initializer = new ConfigInitializer_TestConfig(loaders);
    VALUES.putAll(initializer.initialize());
  }

  @Override
  public String aaa() {
    return (String)VALUES.get("aaa");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> dlist() {
    return (List<String>)VALUES.get("test.dlist");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> dlist2() {
    return (List<String>)VALUES.get("test.dlist2");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<String> dset() {
    return (Set<String>)VALUES.get("test.dset");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<String> dset2() {
    return (Set<String>)VALUES.get("test.dset2");
  }

  @Override
  @SuppressWarnings("unchecked")
  public SortedSet<String> dsort() {
    return (SortedSet<String>)VALUES.get("test.dsort");
  }

  @Override
  @SuppressWarnings("unchecked")
  public SortedSet<String> dsort2() {
    return (SortedSet<String>)VALUES.get("test.dsort2");
  }

  @Override
  public String dstr() {
    return (String)VALUES.get("test.dstr");
  }

  @Override
  public String dstr2() {
    return (String)VALUES.get("test.dstr2");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Duration> duration() {
    return (Optional<Duration>)VALUES.get("test.duration");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> list() {
    return (List<String>)VALUES.get("test.list");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<List<String>> olist() {
    return (Optional<List<String>>)VALUES.get("test.olist");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<List<String>> olist2() {
    return (Optional<List<String>>)VALUES.get("test.olist2");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Set<String>> oset() {
    return (Optional<Set<String>>)VALUES.get("test.oset");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Set<String>> oset2() {
    return (Optional<Set<String>>)VALUES.get("test.oset2");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<SortedSet<String>> osort() {
    return (Optional<SortedSet<String>>)VALUES.get("test.osort");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<SortedSet<String>> osort2() {
    return (Optional<SortedSet<String>>)VALUES.get("test.osort2");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<String> ostr() {
    return (Optional<String>)VALUES.get("ostr");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<String> ostr1() {
    return (Optional<String>)VALUES.get("test.ostr1");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<String> set() {
    return (Set<String>)VALUES.get("test.set");
  }

  @Override
  @SuppressWarnings("unchecked")
  public SortedSet<String> sort() {
    return (SortedSet<String>)VALUES.get("test.sort");
  }

  @Override
  public String str() {
    return (String)VALUES.get("test.string");
  }

  @Override
  public String testconverter() {
    return (String)VALUES.get("test.testconverter");
  }

  @Override
  public int hashCode() {
    return Objects.hash(aaa(), dlist(), dlist2(), dset(), dset2(), dsort(), dsort2(), dstr(), dstr2(), duration(), list(), olist(), olist2(), oset(), oset2(), osort(), osort2(), ostr(), ostr1(), set(), sort(), str(), testconverter());
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append("aaa").append('=').append(String.valueOf(VALUES.get("aaa")));
    buf.append(", ");
    buf.append("dlist").append('=').append(String.valueOf(VALUES.get("test.dlist")));
    buf.append(", ");
    buf.append("dlist2").append('=').append(String.valueOf(VALUES.get("test.dlist2")));
    buf.append(", ");
    buf.append("dset").append('=').append(String.valueOf(VALUES.get("test.dset")));
    buf.append(", ");
    buf.append("dset2").append('=').append(String.valueOf(VALUES.get("test.dset2")));
    buf.append(", ");
    buf.append("dsort").append('=').append(String.valueOf(VALUES.get("test.dsort")));
    buf.append(", ");
    buf.append("dsort2").append('=').append(String.valueOf(VALUES.get("test.dsort2")));
    buf.append(", ");
    buf.append("dstr").append('=').append(String.valueOf(VALUES.get("test.dstr")));
    buf.append(", ");
    buf.append("dstr2").append('=').append(String.valueOf(VALUES.get("test.dstr2")));
    buf.append(", ");
    buf.append("duration").append('=').append(String.valueOf(VALUES.get("test.duration")));
    buf.append(", ");
    buf.append("list").append('=').append(String.valueOf(VALUES.get("test.list")));
    buf.append(", ");
    buf.append("olist").append('=').append(String.valueOf(VALUES.get("test.olist")));
    buf.append(", ");
    buf.append("olist2").append('=').append(String.valueOf(VALUES.get("test.olist2")));
    buf.append(", ");
    buf.append("oset").append('=').append(String.valueOf(VALUES.get("test.oset")));
    buf.append(", ");
    buf.append("oset2").append('=').append(String.valueOf(VALUES.get("test.oset2")));
    buf.append(", ");
    buf.append("osort").append('=').append(String.valueOf(VALUES.get("test.osort")));
    buf.append(", ");
    buf.append("osort2").append('=').append(String.valueOf(VALUES.get("test.osort2")));
    buf.append(", ");
    buf.append("ostr").append('=').append(String.valueOf(VALUES.get("ostr")));
    buf.append(", ");
    buf.append("ostr1").append('=').append(String.valueOf(VALUES.get("test.ostr1")));
    buf.append(", ");
    buf.append("set").append('=').append(String.valueOf(VALUES.get("test.set")));
    buf.append(", ");
    buf.append("sort").append('=').append(String.valueOf(VALUES.get("test.sort")));
    buf.append(", ");
    buf.append("str").append('=').append(String.valueOf(VALUES.get("test.string")));
    buf.append(", ");
    buf.append("testconverter").append('=').append(String.valueOf(VALUES.get("test.testconverter")));
    buf.append(']');
    return buf.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Config_TestConfig)) return false;
    Config_TestConfig other = (Config_TestConfig) o;
    if (!this.aaa().equals(other.aaa())) return false;
    if (!this.dlist().equals(other.dlist())) return false;
    if (!this.dlist2().equals(other.dlist2())) return false;
    if (!this.dset().equals(other.dset())) return false;
    if (!this.dset2().equals(other.dset2())) return false;
    if (!this.dsort().equals(other.dsort())) return false;
    if (!this.dsort2().equals(other.dsort2())) return false;
    if (!this.dstr().equals(other.dstr())) return false;
    if (!this.dstr2().equals(other.dstr2())) return false;
    if (!this.duration().equals(other.duration())) return false;
    if (!this.list().equals(other.list())) return false;
    if (!this.olist().equals(other.olist())) return false;
    if (!this.olist2().equals(other.olist2())) return false;
    if (!this.oset().equals(other.oset())) return false;
    if (!this.oset2().equals(other.oset2())) return false;
    if (!this.osort().equals(other.osort())) return false;
    if (!this.osort2().equals(other.osort2())) return false;
    if (!this.ostr().equals(other.ostr())) return false;
    if (!this.ostr1().equals(other.ostr1())) return false;
    if (!this.set().equals(other.set())) return false;
    if (!this.sort().equals(other.sort())) return false;
    if (!this.str().equals(other.str())) return false;
    if (!this.testconverter().equals(other.testconverter())) return false;
    return true;
  }
}
