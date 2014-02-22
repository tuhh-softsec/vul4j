require "rest-client"
require "optparse"

options = { 
  :rest_op => "add",
  :intent_id => 1, 
  :intent_category => "high",
  :intent_type => "shortest_intent_type", 
  :max_switches => 4,
  :intent_op => "add"
}

parser = OptionParser.new do |opts|
  opts.banner = "Usage [get-options] [post-options]"

  opts.separator  ""
  opts.separator  "Get options:"

  opts.on('-G', '--get_intents', 'get intents state') do
    options[:get_intents] = true
    options[:rest_op] = "get"
  end
  opts.on('-g', '--get_intent intent_id', 'get intent state') do |intent_id|
    options[:rest_op] = "get"
    options[:get_intent] = intent_id
  end

  opts.separator  ""
  opts.separator  "Post options:"

  opts.on('-t', '--max_intents max_intents', 'max. number of intents') do |max_intents|
    options[:max_intents] = max_intents
  end
  opts.on('-e', '--intent_category intent_category', 'intent category high|low') do |intent_category|
    options[:intent_category] = intent_category
  end
  opts.on('-i', '--intent_id intent_id', 'global intent id') do |id|
    options[:intent_id] = id.to_i
  end
  # optional argument
  opts.on('-s', '--shortest', 'create a shortest path intent') do
    options[:intent_type] = "shortest_intent_type"
  end
  # optional argument
  opts.on('-c', '--constrained', 'create a constrained shortest path intent') do
    options[:intent_type] = "constrained_shortest_intent_type"
  end
  # optional argument
  opts.on('-r', '--random_intent', 'create minimum no. of random intents') do
    options[:random_intent] = true
  end
  opts.on('-m', '--max_switches max_switches', 'max. number of switches') do |max_switches|
    options[:max_switches] = max_switches.to_i
  end
  opts.on('-o', '--intent_op add|remove', 'an operation to post an intent') do |operation|
    options[:intent_op] = operation
  end
  opts.on('-w', '--server server', 'server to post intents') do |server|
    options[:server] = server
  end
  opts.on('-p','--port port', 'server port') do |port|
    options[:port] = port
  end
  opts.on('b', '--bulk_limit bulk_limit', 'bulk request upto this limit') do |bulk_limit|
    options[:bulk_limit] = bulk_limit
  end
  opts.on('-h', '--help', 'Display help') do
    puts opts
    exit
  end
end
parser.parse!


class Intent
  attr_reader :switches, :ports, :intent_id
  attr_reader :intent_type, :intent_op
  attr_reader :random_intent, :server, :port
  attr_reader :bulk_limit

  def initialize options
    parse_options options
  end

  def post_intent 
    create_specific_intent
  end

  def get_intent options
    if options[:get_intents] == true
      request = RestClient.get "http://#{@server}:#{@port}/wm/onos/datagrid/get/intents/#{options[:intent_category]}/json"
    else
      url = "http://#{@server}:#{@port}/wm/onos/datagrid/get/intent/#{options[:intent_category]}/#{options[:get_intent]}/json"
      request = RestClient.get url
    end
    puts request
  end

  private 

  def create_specific_intent
    if @random_intent == true
      create_random_intent
    else
      create_many_intents
    end
  end

  # create as many intents as the number of switches
  def create_many_intents
    intents = []
    @switches.each do |sw|
      rest = @switches - [sw]
      intents = _create_intent sw, rest, intents
puts intents.size
      post_slice intents
    end
    post_slice intents, true
  end

  # pick a random src switch and create intents to all other switches
  def create_random_intent
    intents = []
    sw = @switches.shuffle[0]
    rest = @switches - [sw]
    intents = _create_intent sw, rest, intents
    post_slice intents, true
  end

  def post_slice intents, last=false
    @bulk_limit = @bulk_limit.to_i
    if intents.size >= @bulk_limit
      post intents.slice!(0..(@bulk_limit - 1))
    end
    if last == true
      loop do
        new_bulk_limit = intents.size > @bulk_limit ? @bulk_limit : intents.size
        post intents.slice!(0..(new_bulk_limit - 1))
        break if new_bulk_limit < @bulk_limit
      end
    end
  end

  def post intents
    json_data = intents.to_json
    response = RestClient.post "http://#{@server}:#{@port}/wm/onos/datagrid/#{intent_op}/intents/json", json_data, :content_type => :json, :accept => :json
    puts response
  end

  def parse_options options
    max_switches = options[:max_switches].to_i || 4
    @switches = (1..max_switches).to_a
    @ports = (1..max_switches).to_a
    @intent_id = options[:intent_id]
    @intent_id ||= 1
    @intent_type = options[:intent_type]
    @intent_op = options[:intent_op]
    @intent_op ||= "add"
    @random_intent = options[:random_intent]
    @random_intent ||= false
    @server = options[:server]
    @server ||= "127.0.0.1"
    @port = options[:port]
    @port ||= 8080
    @bulk_limit = options[:bulk_limit]
    @bulk_limit ||= 10000
  end


  def _create_intent src_switch, iterable_switches, json_intents
    network_id = 1
    iterable_switches.each_index do |sw_i|
      intent = {
        :intent_id => "#{@intent_id}",
        :intent_type => @intent_type,
        :intent_op => @intent_op,
        :srcSwitch => src_switch.to_s,
        :srcPort => @ports[-1],
        :srcMac => "00:00:c0:a8:#{mac_format(src_switch)}",
        :dstSwitch => iterable_switches[sw_i].to_s,
        :dstPort => @ports[-1],
        :dstMac => "00:00:c0:a8:#{mac_format(iterable_switches[sw_i].to_i)}"
      }
puts intent.inspect
      @intent_id = @intent_id + 1
      json_intents << intent
puts
    end
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
if options[:rest_op] == "get"
  intent.get_intent options
else
  json_data = intent.post_intent
end

