# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding field 'Song.song_name'
        db.add_column(u'myapp_song', 'song_name',
                      self.gf('django.db.models.fields.CharField')(default='herro', max_length=100),
                      keep_default=False)

        # Adding field 'Song.artists'
        db.add_column(u'myapp_song', 'artists',
                      self.gf('django.db.models.fields.CharField')(default='artists', max_length=100),
                      keep_default=False)

        # Adding field 'Song.image_url'
        db.add_column(u'myapp_song', 'image_url',
                      self.gf('django.db.models.fields.CharField')(default='image_url', max_length=1000),
                      keep_default=False)


    def backwards(self, orm):
        # Deleting field 'Song.song_name'
        db.delete_column(u'myapp_song', 'song_name')

        # Deleting field 'Song.artists'
        db.delete_column(u'myapp_song', 'artists')

        # Deleting field 'Song.image_url'
        db.delete_column(u'myapp_song', 'image_url')


    models = {
        u'myapp.party': {
            'Meta': {'object_name': 'Party'},
            'host': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.User']"}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'unique_code': ('django.db.models.fields.CharField', [], {'max_length': '6'})
        },
        u'myapp.song': {
            'Meta': {'object_name': 'Song'},
            'artists': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'image_url': ('django.db.models.fields.CharField', [], {'max_length': '1000'}),
            'party': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.Party']"}),
            'song_name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'spotify_uri': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'vote_count': ('django.db.models.fields.IntegerField', [], {})
        },
        u'myapp.user': {
            'Meta': {'object_name': 'User'},
            'current_party': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.Party']", 'null': 'True'}),
            'email': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'last_login': ('django.db.models.fields.DateField', [], {'null': 'True'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '30'})
        }
    }

    complete_apps = ['myapp']