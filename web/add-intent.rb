require "rest-client"
require "optparse"

options = { :intent_id => 123, :intent_type => "shortest_intent_type", :max_switches => 4 }

parser = OptionParser.new do |opts|
  opts.banner = "Usage add-intent [options]"
  opts.on('-t', '--max_intents max_intents', 'max. number of intents') do |max_intents|
    options[:max_intents] = max_intents
  end
  opts.on('-a', '--application application_id', 'set application id') do |appl_id|
    options[:application_id] = appl_id.to_i
  end
  opts.on('-i', '--intent_id intent_id', 'global intent id') do |id|
    options[:intent_id] = id.to_i
  end
  opts.on('-s', '--shortest', 'create a shortest path intent') do
    options[:intent_type] = "shortest_intent_type"
  end
  opts.on('-c', '--constrained', 'create a constrained shortest path intent') do
    options[:intent_type] = "constrained_shortest_intent_type"
  end
  opts.on('-m', '--max_switches max_switches', 'max. number of switches') do |max_switches|
    options[:max_switches] = max_switches.to_i
  end
  opts.on('-h', '--help', 'Display help') do
    puts opts
    exit
  end
end
parser.parse!

puts options.inspect
server = options[:server]
server ||= "127.0.0.1"
port = options[:port]
port ||= 8080

def rand_mac
  mac = `openssl rand -hex 6`
  mac.scan(/(..)/).join(":")
end

def rand_switch
  switch = `openssl rand -hex 5`.chomp
end

class Intent
  attr_reader :switches
  attr_reader :ports
  attr_reader :intent_id
  attr_reader :application_id
  attr_reader :intent_type

  def initialize options
    parse_options options
  end

  def create_intent 
    json_intents = []
    @switches.each do |sw|
      rest = switches - [sw]
      json_intents = _create_intent sw, rest, json_intents
    end
    json_intents
  end

  def parse_options options
    max_switches = options[:max_switches].to_i || 4
    @switches = (1..max_switches).to_a
    @ports = (1..(max_switches - 1)).to_a
    @intent_id = options[:intent_id]
    @intent_id ||= 1
    @application_id = options[:application_id]
    @application_id ||= 1
    @intent_type = options[:intent_type]
  end


  def _create_intent src_switch, iterable_switches, json_intents
    network_id = 1
    iterable_switches.each_index do |sw_i|
      dst_switch = iterable_switches[sw_i]
      sw_set = @switches - [dst_switch]
      dst_port = sw_set.index(src_switch)
      dst_port = dst_port + 1
      intent = {
        :intent_id => "#{@application_id}:#{@intent_id}",
        :intent_type => @intent_type,
        :srcSwitch => src_switch.to_s,
        :srcPort => @ports[sw_i],
        :srcMac => "00:00:c0:a8:#{mac_format(src_switch)}",
        :dstSwitch => iterable_switches[sw_i].to_s,
        :dstPort => dst_port,
        :dstMac => "00:00:c0:a8:#{mac_format(iterable_switches[sw_i].to_i)}"
      }
puts intent
      @intent_id = @intent_id + 1
      json_intents << intent
puts
    end
    #sha256 = Digest::SHA256.new
    #sha256.update intent_hash.to_s
    #puts sha256.hexdigest
    #puts "intent hash = #{intent_hash}"
    json_intents
  end

  def mac_format number
    if number > 255
      divisor = number / 256 
      remainder = number % 256
      return sprintf("%02x:%02x",divisor ,remainder)
    end
    "00:%02x" % number
  end
end

intent = Intent.new options
json_data = intent.create_intent
response = RestClient.post "http://#{server}:#{port}/wm/onos/datagrid/add/intent/json", json_data.to_json, :content_type => :json, :accept => :json
puts response.inspect
