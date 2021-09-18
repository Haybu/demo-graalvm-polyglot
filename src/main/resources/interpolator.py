import java

import java.util.ArrayList
from java.util import ArrayList

import java.lang.Double
from java.lang import Double

import java.time.LocalDateTime
from java.time import LocalDateTime

import java.time.format.DateTimeFormatter
from java.time.format import DateTimeFormatter

#import io.agilehandy.demo.TimeSeries
#from io.agilehandy.demo import TimeSeries

import pandas as pd
import numpy as np
#from datetime import datetime

DATE_FORMAT_FROM_JAVA = 'yyyy-MM-dd:HH:mm:ss'
DATE_FORMAT_FROM_PYTHON = '%Y-%m-%d:%H:%M:%S'

#Double = java.type("java.lang.Double")
#LocalDateTime = java.type("java.time.LocalDateTime")
#DateTimeFormatter = java.type("java.time.format.DateTimeFormatter")
TimeSeries = java.type("io.agilehandy.demo.TimeSeries")
formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_FROM_JAVA)

class Interpolator:
  def __init__(self, _remoteList):
    self.list = _remoteList

  def dataframe(self):
    keyList = []
    datetimeList = []
    valueList = []
    columns = ['datetime', 'key', 'value']
    for x in self.list:
      datetimeList.append(x.time.format(formatter))
      keyList.append(x.key)
      valueList.append(x.value)
    data_dict = {'datetime': datetimeList, 'key': keyList, 'value': valueList}
    df = pd.DataFrame(data_dict, columns=columns)
    print("df shape {}".format(df.shape))
    return df

  def index_df(self, df):
    df['datetime'] = pd.to_datetime(df['datetime'], format=DATE_FORMAT_FROM_PYTHON)
    df = df.set_index('datetime')
    print("indexed df shape {}".format(df.shape))
    return df

  def interpolate_df(self, seconds):
    df = self.dataframe()
    df_indexed = self.index_df(df)
    # linear mean interpolation every x seconds
    #w = str(seconds) + 'S'
    df_interpol = df_indexed.groupby('key').resample('S').mean()
    print("resampled df shape {}".format(df_interpol.shape))
    df_interpol['value'] = df_interpol['value'].interpolate(method='linear')
    return df_interpol

  def df_to_java_timeseries(self, df):
    javaList = ArrayList()
    for index, row in df.iterrows():
      print("key: %s datetime: %s value: %d" % (row['key'], row['datetime'], row['value']))
      ts = TimeSeries(row['datetime'], row['value'], row['key'])
      javaList.add(ts)
    return javaList

  def interpolate(self, seconds):
    df = self.interpolate_df(seconds)
    javaList = self.df_to_java_timeseries(df)
    return javaList

  def echo(self):
    for x in self.list:
      #dt_str = x.time.format(formatter)
      #print(dt_str)
      #datetime_obj = datetime.strptime(dt_str, '%Y-%m-%d:%H:%M:%S')
      print("key: %s datetime: %s value: %d" % (x.key, x.time.format(formatter), x.value))

  def sum(self, x, y, z):
    return x + y + z
