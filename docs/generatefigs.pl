#!/usr/bin/perl -w
# perl hacked... script to create nice looking xfig images
# of packets
# fig2dev -L gif -b 10 -t white -m 2 input.fig output.gif
# $Id: generatefigs.pl,v 1.1 2002/11/03 11:01:00 keesj Exp $
use strict;

use IO::File;

sub createfig(@){
	my @packet= @_;
	my $output="";
	
$output .= "#FIG 3.2\n";
$output .= "Landscape\n";
$output .= "Center\n";
$output .= "Inches\n";
$output .= "Letter\n";
$output .= "100.00\n";
$output .= "Single\n";
$output .= "-2\n";
$output .= "1200 2\n";

my $counter =0;

my $size = scalar(@packet);

for(my $counter =0 ; $counter < @packet ; $counter+=2){

	my $x1 = 150 + $counter * 150 ;
	my $y1 = 225;
	my $x2 = $x1;
	my $y2 = $y1 + (150 * $size) -(150 * $counter);

	my $x3 = 300 + ($size -1) * 150;
	my $y3 = $y2;
	my $textx= $x3 + 150;
	my $texty= $y3 + 75;
	my $text= $packet[$counter +1 ];
	my $textwidth = length($text) * 105;
	$output .= "2 1 0 1 0 7 50 0 -1 0.000 0 0 -1 1 0 3\n";
	$output .= "\t0 0 1.00 60.00 120.00\n";
	$output .= "\t $x1 $y1 $x2 $y2 $x3 $y3\n";
	$output .= "4 0 0 50 0 14 12 0.0000 4 120 $textwidth $textx $texty $text\\001\n";
}

for(my $counter =0 ; $counter <  @packet  ; $counter+=2){
	$output .= "4 1 0 50 0 14 12 0.0000 4 120 210 " . (150 + 150 * $counter) . " 150 ". $packet[$counter] ."\\001\n";
}
return $output;
}

my @p=(
"C0" => "packet header",
"FF" => "session id",
"B3" => "init command",
"01" => "checksum 1",
"B2" => "checksum 2",
"C1" => "packet trailer",
);

my $fh = new IO::File("> 01-init.fig");
defined $fh || die "can not open file\n";
print $fh  createfig(@p);
$fh->close();

@p=(
"C0" => "packet header",
"FF" => "session id",
"A3" => "session request",
"04" => "hour of the day(int value) 0..23",
"26" => "minutes of the hour(int value) 0..59",
"16" => "seconds of the minute(int value)",
"4E" => "unknown",
"02" => "checksum 1",
"30" => "checksum 2",
"C1" => "packet tailer"
);

$fh = new IO::File("> 02-challenge-request.fig");
defined $fh || die "can not open file\n";
print $fh  createfig(@p);
$fh->close();

@p=(
"C0" => "packet header",
"FF" => "session id",
"93" => "session attribution",
"04" => "hour from previous packet",
"26" => "minutes from previous packet",
"16" => "seconds from previous packet",
"4E" => "unknown, but same as in previous packet",
"4B" => "session id that will be used for the rest of the comunication",
"02" => "checksum 1",
"6B" => "checksum 2",
"C1" => "packet trailer"
);

$fh = new IO::File("> 03-session-initiation.fig");
defined $fh || die "can not open file\n";
print $fh  createfig(@p);
$fh->close();
