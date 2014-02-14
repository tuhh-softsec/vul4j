require "rest-client"
require "optparse"


options = { :intent_id => 123, :path_intent => "shortest_path_intent", :max_switches => 4 }

parser = OptionParser.new do |opts|
  opts.banner = "Usage add-intent [options]"
  opts.on('-t', '--max_intents max_intents', 'max. number of intents') do |max_intents|
    options[:max_intents] = max_intents
  end
  opts.on('-a', '--application application_id', 'set application id') do |appl_id|
    options[:application_id] = appl_id
  end
  opts.on('-i', '--intent_id intent_id', 'global intent id') do |id|
    options[:intent_id] = id
  end
  opts.on('-s', '--shortest path intent', 'create a shortest path intent') do
    options[:path_intent] = "shortest_path_intent"
  end
  opts.on('-c', '--constrained shortest path intent', 'create a constrained shortest path intent') do |cspi|
    options[:path_intent] = "constrained_shortest_path_intent"
  end
  opts.on('-m', '--max_switches max_switches', 'max. number of switches') do |max_switches|
    options[:max_switches] = max_switches;
  end
  opts.on('-h', '--help', 'Display help') do
    puts opts
    exit
  end
end
parser.parse!

puts options.inspect
server = options[:server] || "127.0.0.1"
port = options[:port] || 8080

def rand_mac
  mac = `openssl rand -hex 6`
  mac.scan(/(..)/).join(":")
end

def rand_switch
  switch = `openssl rand -hex 5`.chomp
end

class Intent
  attr_reader :switches, :ports, :intent_id, :application_id

  def initialize options
    parse_options options
  end

  def create_intent 
    json_intents = []
    @switches.each do |sw|
      rest = switches - [sw]
      json_intents = _create_intent sw, rest, json_intents
    end
puts json_intents.inspect
  end

  def parse_options options
    max_switches = options[:max_switches].to_i || 4
    @switches = (1..max_switches).to_a
    @ports = (1..(max_switches - 1)).to_a
    @intent_id = options[:intent_id].to_i || 1
    @application_id = options[:application_id].to_i || 1
  end


  def _create_intent src_switch, iterable_switches, json_intents
    iterable_switches.each_index do |sw_i|
      dst_switch = iterable_switches[sw_i]
      sw_set = @switches - [dst_switch]
      dst_port = sw_set.index(src_switch)
      dst_port = dst_port + 1
      intent = {
        :applicationId => @application_id,
        :intentId => @intent_id,
        :srcSwitch => src_switch.to_s,
        :srcPort => @ports[sw_i],
        :srcMac => "00:00:00:c0:a8:01:#{@ports[sw_i]}",
        :dstSwitch => iterable_switches[sw_i],
        :dstPort => dst_port,
        :dstMac => "00:00:00:c0:a8:01:#{dst_port}"
      }
      @intent_id = @intent_id + 1
      json_intents << intent
    end
    #sha256 = Digest::SHA256.new
    #sha256.update intent_hash.to_s
    #puts sha256.hexdigest
    #puts "intent hash = #{intent_hash}"
    json_intents
  end
end

# the program accepts the number of switches and ports and outputs a number of intents
json_data = [{
  :intentId => 12345,
  :type => "shortest-path", 
  :srcSwitch => "0x0000000000000001", 
  :srcPort => 1, 
  :srcMac =>"#{rand_mac}", 
  :dstSwitch => "0x0000000000000002",
  :dstPort => 4, 
  :dstMac => "00:00:00:00:00:02"} 
#  {:type => "constrained-shortest-path", 
#  :srcSwitch => "0x#{rand_switch}",
#  :srcPort => 2, 
#  :srcMac => "00:00:00:00:00:11", 
#  :dstSwitch => "0x#{rand_switch}",
#  :dstPort => 3, 
#  :dstMac => "00:00:00:00:00:22", 
#  :bandwidth => 5.0 }
]
#puts json_data.to_json


ports = [1,2,3]
switches = [1,2,3,4]
intent = Intent.new options
json_data = intent.create_intent
#response = RestClient.post 'http://#{server}:#{port}/wm/onos/datagrid/add/intent/json', json_data.to_json, :content_type => :json, :accept => :json
#puts response.inspect
